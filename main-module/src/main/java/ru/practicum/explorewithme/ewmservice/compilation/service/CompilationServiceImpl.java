package ru.practicum.explorewithme.ewmservice.compilation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.ewmservice.common.PageRequestHelper;
import ru.practicum.explorewithme.ewmservice.compilation.dao.CompilationDao;
import ru.practicum.explorewithme.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.dto.UpdateCompilationDto;
import ru.practicum.explorewithme.ewmservice.compilation.mapper.CompilationDtoMapper;
import ru.practicum.explorewithme.ewmservice.compilation.mapper.NewCompilationDtoMapper;
import ru.practicum.explorewithme.ewmservice.compilation.model.Compilation;
import ru.practicum.explorewithme.ewmservice.event.dao.EventDao;
import ru.practicum.explorewithme.ewmservice.event.dto.EventShortDto;
import ru.practicum.explorewithme.ewmservice.event.mapper.EventShortDtoMapper;
import ru.practicum.explorewithme.ewmservice.event.model.Event;
import ru.practicum.explorewithme.ewmservice.exception.NotFoundException;
import ru.practicum.explorewithme.ewmservice.request.service.RequestService;
import ru.practicum.explorewithme.statsclient.StatsServiceClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationDao compilationDao;
    private final EventDao eventDao;
    private final StatsServiceClient statsServiceClient;
    private final RequestService requestService;
    private final NewCompilationDtoMapper newCompilationDtoMapper;
    private final EventShortDtoMapper eventShortDtoMapper;
    private final CompilationDtoMapper compilationDtoMapper;
    private final ObjectMapper objectMapper;

    private static List<Long> getEventsId(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto get(Long compId) {
        Compilation compilation = getCompilationOrThrowException(compId);
        List<Event> events = compilation.getEvents();
        List<Long> eventsIds = getEventsId(events);

        Map<Long, Integer> countRequestsByEventId = requestService.countConfirmedRequests(eventsIds);
        Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);

        List<EventShortDto> shortDtoList = events.stream()
                .map(event -> eventShortDtoMapper.toDto(event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());

        return Optional.of(compilation)
                .map(compilationDao::save)
                .map(c -> compilationDtoMapper.toDto(c, shortDtoList))
                .get();
    }

    @Override
    public List<CompilationDto> get(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> compilations = compilationDao.findAllCompilationsOptionalPinned(
                pinned,
                new PageRequestHelper(from, size, null)
        );

        List<Long> eventsIds = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .map(Event::getId).distinct().collect(Collectors.toList());

        Map<Long, Integer> countRequestsByEventId = requestService.countConfirmedRequests(eventsIds);
        Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);

        Map<Long, List<EventShortDto>> eventShortDtoMap = compilations.stream()
                .collect(Collectors.toMap(
                        Compilation::getId,
                        comp -> comp.getEvents().stream()
                                .map(event -> eventShortDtoMapper.toDto(
                                        event,
                                        countRequestsByEventId.get(event.getId()),
                                        statisticMap.get(event.getId())))
                                .collect(Collectors.toList())
                ));
        return compilations.stream()
                .map(comp -> compilationDtoMapper.toDto(comp, eventShortDtoMap.get(comp.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto dto) {
        List<Event> events;
        List<EventShortDto> shortDtoList;

        if (dto != null && dto.getEvents() != null) {
            events = eventDao.findAllByIdIn(dto.getEvents());
            List<Long> eventsIds = getEventsId(events);
            Map<Long, Integer> countRequestsByEventId = requestService.countConfirmedRequests(eventsIds);
            Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                    LocalDateTime.now().plusYears(100), eventsIds);
            shortDtoList = events.stream()
                    .map(event -> eventShortDtoMapper.toDto(event,
                            countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                            statisticMap.get(event.getId())))
                    .collect(Collectors.toList());
        } else {
            events = null;
            shortDtoList = new ArrayList<>();
        }
        return Optional.ofNullable(dto)
                .map(compilationDto -> newCompilationDtoMapper.toModel(compilationDto, events))
                .map(compilationDao::save)
                .map(compilation -> compilationDtoMapper.toDto(compilation, shortDtoList))
                .get();
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (!compilationDao.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
        compilationDao.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationDto dto) {
        Compilation compilation = getCompilationOrThrowException(compId);
        List<EventShortDto> eventDtos;
        List<Event> events = compilation.getEvents();

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            events = eventDao.findAllByIdIn(dto.getEvents());
            compilation.setEvents(events);
        }

        List<Long> eventsIds = getEventsId(events);

        Map<Long, Integer> countRequestsByEventId = requestService.countConfirmedRequests(eventsIds);
        Map<Long, Long> statisticMap = getEventStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);
        eventDtos = events.stream()
                .map(event -> eventShortDtoMapper.toDto(event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());


        return Optional.of(compilation)
                .map(compilationDao::save)
                .map(comp -> compilationDtoMapper.toDto(comp, eventDtos))
                .get();
    }

    private Compilation getCompilationOrThrowException(Long compId) {
        return compilationDao.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compId + " not found"));
    }

    private Map<Long, Long> getEventStatisticMap(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> eventsIds) {
        List<String> uris = eventsIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        ResponseEntity<Object> stats = statsServiceClient.stats(rangeStart, rangeEnd, uris, false);
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
}
