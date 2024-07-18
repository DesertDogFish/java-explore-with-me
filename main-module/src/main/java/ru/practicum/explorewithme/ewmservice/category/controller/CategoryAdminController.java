package ru.practicum.explorewithme.ewmservice.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryShortDto;
import ru.practicum.explorewithme.ewmservice.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto save(@Valid @RequestBody CategoryShortDto categoryShortDto) {
        return categoryService.save(categoryShortDto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto patch(@Valid @RequestBody CategoryShortDto categoryShortDto,
                             @PathVariable Long categoryId) {
        return categoryService.patch(categoryId, categoryShortDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }
}
