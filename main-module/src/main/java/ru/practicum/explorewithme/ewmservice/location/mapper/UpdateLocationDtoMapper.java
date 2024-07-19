package ru.practicum.explorewithme.ewmservice.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.location.dto.NewLocationDto;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

@Mapper(componentModel = "spring")
public interface UpdateLocationDtoMapper {
    Location toModel(NewLocationDto newLocationDto);
}
