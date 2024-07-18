package ru.practicum.explorewithme.ewmservice.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryShortDto;
import ru.practicum.explorewithme.ewmservice.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryShortDtoMapper {
    Category toModel(CategoryShortDto categoryShortDto);
}
