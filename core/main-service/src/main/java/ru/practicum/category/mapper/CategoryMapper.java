package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.in.NewCategoryDto;
import ru.practicum.category.output.CategoryDto;
import ru.practicum.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto category);
}