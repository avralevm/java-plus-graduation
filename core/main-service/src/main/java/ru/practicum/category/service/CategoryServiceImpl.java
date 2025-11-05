package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.in.NewCategoryDto;
import ru.practicum.category.output.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto add(NewCategoryDto newCategory) {
        checkCategoryNameExists(newCategory.getName());
        Category category = categoryRepository.save(mapper.toCategory(newCategory));
        return mapper.toCategoryDto(category);
    }

    @Override
    public void delete(Long id) {
        Category category = getCategoryOrThrow(id);

        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException(String.format("Cannot delete category with id=%d because it has linked events", id));
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto update(Long id, NewCategoryDto newCategory) {
        Category existingCategory = getCategoryOrThrow(id);

        if (!existingCategory.getName().equals(newCategory.getName())) {
            checkCategoryNameExists(newCategory.getName());
        }

        existingCategory.setName(newCategory.getName());

        Category updatedCategory = categoryRepository.save(existingCategory);
        log.info("Category was updated with id={}, old name='{}', new name='{}'",
                id, existingCategory.getName(), newCategory.getName());
        return mapper.toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> findAll(int from, int size) {
        List<Category> categories;

        if (from < size && size > 0) {
            int pageNumber = from / size;
            Pageable pageable = PageRequest.of(pageNumber, size);
            Page<Category> page = categoryRepository.findAll(pageable);
            categories = page.getContent();
        } else if (size == 0) {
            categories = categoryRepository.findAll().stream()
                    .skip(from)
                    .toList();
        } else {
            return List.of();
        }

        return categories.stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = getCategoryOrThrow(id);
        return mapper.toCategoryDto(category);
    }

    private void checkCategoryNameExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateException("Category already exists: " + name);
        }
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", id)));
    }
}