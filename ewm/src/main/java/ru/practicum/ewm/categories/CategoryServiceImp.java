package ru.practicum.ewm.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryMapper;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.error.IntegrityConflictException;
import ru.practicum.ewm.events.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<Category> getCategories(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepository.findAll(page).stream()
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category with id=%d was not found", id),
                        "The required object was not found.")
        );
    }

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
            return CategoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityConflictException(e.getMessage(), "Integrity constraint has been violated.");
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category with id=%d was not found", catId),
                        "The required object was not found.")
        );
        if (eventRepository.findByCategoryId(catId) != null) {
            throw new IntegrityConflictException("The category is not empty",
                    "For the requested operation the conditions are not met.");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category with id=%d was not found", catId),
                        "The required object was not found.")
        );
        try {
            category.setName(categoryDto.getName());
            Category updatedCategory = categoryRepository.save(category);
            return CategoryMapper.toCategoryDto(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityConflictException(e.getMessage(), "Integrity constraint has been violated.");
        }
    }
}
