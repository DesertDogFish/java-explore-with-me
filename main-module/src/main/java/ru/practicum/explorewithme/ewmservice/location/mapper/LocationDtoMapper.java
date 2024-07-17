package ru.practicum.explorewithme.ewmservice.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationDto;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationDtoMapper {
    Location toModel(LocationDto locationDto);
}
