package ru.practicum.explorewithme.ewmservice.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.ewmservice.request.model.Request;

@Mapper(componentModel = "spring")
public interface ParticipationRequestDtoMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toDto(Request request);
}
