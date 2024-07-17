package ru.practicum.explorewithme.ewmservice.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.ewmservice.category.dao.CategoryDao;
import ru.practicum.explorewithme.ewmservice.category.model.Category;
import ru.practicum.explorewithme.ewmservice.common.DateTimeUtil;
import ru.practicum.explorewithme.ewmservice.common.PageRequestHelper;
import ru.practicum.explorewithme.ewmservice.event.dao.EventDao;
import ru.practicum.explorewithme.ewmservice.event.dto.*;
import ru.practicum.explorewithme.ewmservice.event.mapper.EventFullDtoMapper;
import ru.practicum.explorewithme.ewmservice.event.mapper.EventShortDtoMapper;
import ru.practicum.explorewithme.ewmservice.event.mapper.NewEventDtoMapper;
import ru.practicum.explorewithme.ewmservice.event.model.Event;
import ru.practicum.explorewithme.ewmservice.event.model.enums.EventState;
import ru.practicum.explorewithme.ewmservice.event.model.enums.SearchEventValues;
import ru.practicum.explorewithme.ewmservice.exception.ConditionsNotMetException;
import ru.practicum.explorewithme.ewmservice.exception.ConflictException;
import ru.practicum.explorewithme.ewmservice.exception.NotFoundException;
import ru.practicum.explorewithme.ewmservice.location.model.Location;
import ru.practicum.explorewithme.ewmservice.location.service.LocationService;
import ru.practicum.explorewithme.ewmservice.request.dao.RequestDao;
import ru.practicum.explorewithme.ewmservice.request.model.enums.RequestStatus;
import ru.practicum.explorewithme.ewmservice.request.service.RequestService;
import ru.practicum.explorewithme.ewmservice.user.dao.UserDao;
import ru.practicum.explorewithme.ewmservice.user.model.User;
import ru.practicum.explorewithme.statsclient.StatsServiceClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EventServiceImpl implements EventService {
    private final UserDao userDao;
    private final CategoryDao categoryDao;
    private final EventDao eventDao;
    private final RequestDao requestDao;
    private final LocationService locationService;
    private final RequestService requestService;
    private final NewEventDtoMapper newEventDtoMapper;
    private final EventFullDtoMapper eventFullDtoMapper;
    private final EventShortDtoMapper eventShortDtoMapper;
    private final StatsServiceClient statsServiceClient;
    private final ObjectMapper objectMapper;

    private static List<Long> extractEventsId(Page<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto saveEvent(Long userId, NewEventDto dto) {
        User user = getUserOrThrowNotFoundException(userId);
        checkTimeThrowNotCorrectTimeException(dto.getEventDate(), 2);
        Category category = getCategoryOrThrowNotFoundException(dto.getCategory());
        Location location = locationService.save(dto.getLocation());

        return Optional.of(dto)
                .map(d -> newEventDtoMapper.toModel(d, category, user, location))
                .map(eventDao::save)
                .map(event -> eventFullDtoMapper.toDto(event, 0, 0L))
                .get();
    }

    @Override
    @Transactional
    public EventFullDto patchByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = getEventOrThrowNotFoundException(eventId);

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(getCategoryOrThrowNotFoundException(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            checkTimeThrowNotCorrectTimeException(dto.getEventDate(), 2);
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.save(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING)) {
                switch (dto.getStateAction()) {
                    case PUBLISH_EVENT:
                        checkTimeThrowNotCorrectTimeException(event.getEventDate(), 1);
                        event.setState(EventState.PUBLISHED);
                        break;
                    case REJECT_EVENT:
                        event.setState(EventState.CANCELED);
                        break;
                }
            } else {
                throw new ConflictException(
                        String.format("Cannot publish the event because it's not in the right state: %s",
                                event.getState()));
            }
        }
        event.setPublishedOn(LocalDateTime.now());
        return eventFullDtoMapper.toDto(event, 0, 0L);
    }

    @Override
    public EventFullDto patchEventByUser(Long userId, Long eventId,
                                         UpdateEventUserRequest dto) {
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            event.setCategory(getCategoryOrThrowNotFoundException(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            checkTimeThrowNotCorrectTimeException(dto.getEventDate(), 2);
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.save(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED)) {
                switch (dto.getStateAction()) {
                    case SEND_TO_REVIEW:
                        event.setState(EventState.PENDING);
                        break;
                    case CANCEL_REVIEW:
                        event.setState(EventState.CANCELED);
                }
            } else {
                throw new ConditionsNotMetException(
                        String.format("Cannot publish the event because it's not in the right state: %s",
                                event.getState()));
            }
        }

        return eventFullDtoMapper.toDto(event, 0, 0L);
    }

    @Override
    public List<EventFullDto> getByAdmin(List<Long> users,
                                         List<EventState> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(2000);
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(2000);
        }

        Page<Event> events = eventDao.getAllEventsForAdmin(
                rangeStart,
                rangeEnd,
                categories,
                states,
                users,
                new PageRequestHelper(from, size, null)
        );

        List<Long> eventsIds = extractEventsId(events);
        Map<Long, Integer> confirmedRequestCount = requestService.countConfirmedRequests(eventsIds);
        Map<Long, Long> statisticMap = getEventStatisticMap(rangeStart, rangeEnd, eventsIds);

        return events.stream()
                .map(event -> eventFullDtoMapper.toDto(
                        event,
                        confirmedRequestCount.get(event.getId()) == null ? 0 : confirmedRequestCount.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllEventsByOwner(Long userId, Integer from, Integer size) {
        getUserOrThrowNotFoundException(userId);
        Page<Event> events = eventDao.findAllByInitiatorId(userId, new PageRequestHelper(from, size, null));
        List<Long> eventsIds = extractEventsId(events);
        Map<Long, Integer> countRequestsByEventId = requestService.countConfirmedRequests(eventsIds);
        Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);
        return events.stream()
                .map(event -> eventShortDtoMapper.toDto(event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventForOwner(Long userId, Long eventId) {
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        checkIfUserIsEventOwnerAndThrowException(event, userId);

        Integer requestCount = requestDao.countAllRequestByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), List.of(eventId));

        return eventFullDtoMapper.toDto(event, requestCount, statisticMap.get(eventId));
    }

    @Override
    public List<EventFullDto> getAllEventsForPublic(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            SearchEventValues sort,
            Integer from,
            Integer size,
            HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        Page<Event> events = eventDao.getAllEventsForPublic(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                EventState.PUBLISHED,
                new PageRequestHelper(from, size, sort.equals(SearchEventValues.UNSORTED) ?
                        null : Sort.by("eventDate"))
        );

        List<Long> eventsIds = extractEventsId(events);
        Map<Long, Integer> countRequestsByEventId = requestService.countConfirmedRequests(eventsIds);
        Map<Long, Long> statisticMap = getEventStatisticMap(rangeStart, rangeEnd, eventsIds);

        statsServiceClient.hit(EndpointHitDto.builder()
                .app("main")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build());

        List<EventFullDto> collect = events.stream()
                .map(event -> eventFullDtoMapper.toDto(
                        event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());
        if (sort.equals(SearchEventValues.VIEWS)) {
            return collect.stream()
                    .sorted(Comparator.comparingLong(EventFullDto::getViews))
                    .collect(Collectors.toList());
        }
        return collect;
    }

    @Override
    public EventFullDto getEventForPublic(Long eventId, HttpServletRequest request) {
        Event event = eventDao.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Integer countRequest = requestDao.countAllRequestByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), List.of(eventId));

        statsServiceClient.hit(EndpointHitDto.builder()
                .app("main")
                .uri("/events/" + eventId)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build());

        return eventFullDtoMapper.toDto(event, countRequest, statisticMap.get(eventId));
    }

    private Map<Long, Long> getEventStatisticMap(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> eventsIds) {
        List<String> uris = eventsIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        ResponseEntity<Object> stats = statsServiceClient.stats(rangeStart, rangeEnd, uris, true);
        List<ViewStatsDto> statisticDtos;
        if (stats.getStatusCode().is2xxSuccessful()) {
            statisticDtos = objectMapper.convertValue(stats.getBody(), new TypeReference<>() {
            });
        } else {
            throw new RuntimeException(Objects.requireNonNull(stats.getBody()).toString());
        }

        return statisticDtos.stream()
                .collect(Collectors.toMap(
                        dto -> extractIdFromUri(dto.getUri()),
                        ViewStatsDto::getHits,
                        Long::sum)
                );
    }

    private Long extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private Event getEventOrThrowNotFoundException(Long eventId) {
        return eventDao.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private void checkTimeThrowNotCorrectTimeException(LocalDateTime dateTime, Integer hour) {
        if (dateTime.isBefore(LocalDateTime.now().plusHours(hour))) {
            throw new IllegalArgumentException(
                    String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                            DateTimeUtil.formatLocalDateTime(dateTime)));
        }
    }

    private Category getCategoryOrThrowNotFoundException(Long categoryId) {
        return categoryDao.findById(categoryId).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%d was not found", categoryId))
        );
    }

    private User getUserOrThrowNotFoundException(Long userId) {
        return userDao.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", userId))
        );
    }

    private void checkIfUserIsEventOwnerAndThrowException(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of event id=%d", userId, event.getId())
            );
        }
    }
}
