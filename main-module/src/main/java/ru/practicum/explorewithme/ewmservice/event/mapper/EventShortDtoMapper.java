package ru.practicum.explorewithme.ewmservice.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.category.mapper.CategoryDtoMapper;
import ru.practicum.explorewithme.ewmservice.event.dto.EventShortDto;
import ru.practicum.explorewithme.ewmservice.event.model.Event;
import ru.practicum.explorewithme.ewmservice.location.mapper.LocationDtoMapper;
import ru.practicum.explorewithme.ewmservice.user.mapper.UserShortDtoMapper;

@Mapper(componentModel = "spring", uses = {CategoryDtoMapper.class, UserShortDtoMapper.class, LocationDtoMapper.class})
public interface EventShortDtoMapper {
    EventShortDto toDto(Event event, Integer confirmedRequests, Long views);
}
