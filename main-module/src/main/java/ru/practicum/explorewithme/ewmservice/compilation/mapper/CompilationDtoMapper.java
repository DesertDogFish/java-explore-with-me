package ru.practicum.explorewithme.ewmservice.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.model.Compilation;
import ru.practicum.explorewithme.ewmservice.event.dto.EventShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationDtoMapper {
    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation, List<EventShortDto> events);
}
