package ru.practicum.explorewithme.ewmservice.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.ewmservice.category.model.Category;
import ru.practicum.explorewithme.ewmservice.event.dto.NewEventDto;
import ru.practicum.explorewithme.ewmservice.event.model.Event;
import ru.practicum.explorewithme.ewmservice.event.model.enums.EventState;
import ru.practicum.explorewithme.ewmservice.location.model.Location;
import ru.practicum.explorewithme.ewmservice.user.model.User;

@Mapper(componentModel = "spring")
public interface NewEventDtoMapper {
    default EventState mapToPendingState(Object source) {
        return EventState.PENDING;
    }

    @Mapping(target = "state", expression = "java(mapToPendingState(newEventDto))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "description", source = "newEventDto.description")
    @Mapping(target = "paid", source = "newEventDto.paid", defaultValue = "false")
    @Mapping(target = "participantLimit", source = "newEventDto.participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "newEventDto.requestModeration", defaultValue = "true")
    @Mapping(target = "publishedOn", ignore = true)
    Event toModel(NewEventDto newEventDto, Category category, User initiator, Location location);
}
