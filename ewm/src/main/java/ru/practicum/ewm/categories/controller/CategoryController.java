package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.CategoryService;
import ru.practicum.ewm.categories.model.Category;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") int size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable long id) {
        return categoryService.getCategory(id);
    }
}
