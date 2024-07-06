package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.model.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getCategories(int from, int size);

    Category getCategory(Long id);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long id);

    CategoryDto updateCategory(CategoryDto categoryDto, Long catId);
}
