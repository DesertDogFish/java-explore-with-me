package ru.practicum.explorewithme.ewmservice.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewmservice.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryRepository;

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Long categoryId) {
        return categoryRepository.get(categoryId);
    }

    @GetMapping()
    public List<CategoryDto> getAllCategories(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return categoryRepository.get(from, size);
    }

}
