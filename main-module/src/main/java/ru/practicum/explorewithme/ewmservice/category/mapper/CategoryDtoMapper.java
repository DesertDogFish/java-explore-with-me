package ru.practicum.explorewithme.ewmservice.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewmservice.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {
    CategoryDto toDto(Category category);
}
