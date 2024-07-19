package ru.practicum.explorewithme.ewmservice.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationFullDto;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocationFullDtoMapper {
    LocationFullDto toDtoList(Location location);
    List<LocationFullDto> toDtoList(List<Location> location);
}
