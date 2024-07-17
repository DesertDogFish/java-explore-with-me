package ru.practicum.explorewithme.ewmservice.category.service;

import ru.practicum.explorewithme.ewmservice.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryShortDto;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryShortDto categoryShortDto);

    CategoryDto patch(Long categoryId, CategoryShortDto categoryShortDto);

    void delete(Long categoryId);

    CategoryDto get(Long categoryId);

    List<CategoryDto> get(Integer from, Integer size);
}
