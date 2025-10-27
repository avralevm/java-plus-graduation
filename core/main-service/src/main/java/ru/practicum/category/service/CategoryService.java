package ru.practicum.category.service;

import ru.practicum.category.dto.in.NewCategoryDto;
import ru.practicum.category.dto.output.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto category);

    void delete(Long id);

    CategoryDto update(Long id, NewCategoryDto category);

    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(Long id);
}
