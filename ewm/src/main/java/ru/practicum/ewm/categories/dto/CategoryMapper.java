package ru.practicum.ewm.categories.dto;

import ru.practicum.ewm.categories.model.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId() != null ? categoryDto.getId() : null)
                .name(categoryDto.getName() != null ? categoryDto.getName() : null)
                .build();
    }
}
