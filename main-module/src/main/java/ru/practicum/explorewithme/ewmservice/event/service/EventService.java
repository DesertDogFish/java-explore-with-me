package ru.practicum.explorewithme.ewmservice.event.service;


import ru.practicum.explorewithme.ewmservice.event.dto.*;
import ru.practicum.explorewithme.ewmservice.event.model.enums.EventState;
import ru.practicum.explorewithme.ewmservice.event.model.enums.SearchEventValues;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto patchByAdmin(Long eventId, UpdateEventAdminRequest updateEventDto);

    List<EventFullDto> getByAdmin(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Integer from,
                                  Integer size);

    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventsByOwner(Long userId, Integer from, Integer size);

    EventFullDto getEventForOwner(Long userId, Long eventId);

    EventFullDto patchEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventUserRequestDto);

    List<EventFullDto> getAllEventsForPublic(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            SearchEventValues sort,
            Integer from,
            Integer size,
            HttpServletRequest request);

    EventFullDto getEventForPublic(Long eventId, HttpServletRequest request);
}
