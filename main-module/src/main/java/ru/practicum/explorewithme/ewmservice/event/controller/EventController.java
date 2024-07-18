package ru.practicum.explorewithme.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.event.dto.EventFullDto;
import ru.practicum.explorewithme.ewmservice.event.model.enums.SearchEventValues;
import ru.practicum.explorewithme.ewmservice.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explorewithme.ewmservice.common.DateTimeUtil.DATE_TIME_FORMAT;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping()
    public List<EventFullDto> getAllEventsForPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<@Positive Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "UNSORTED") String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        checkDateAndThrowException(rangeStart, rangeEnd);

        SearchEventValues sortBy = SearchEventValues.from(sort)
                .orElseThrow(() -> new IllegalArgumentException("Unknown sort: " + sort));

        return eventService.getAllEventsForPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sortBy, from, size, request
        );
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventForPublic(@PathVariable Long eventId, HttpServletRequest request) {
        return eventService.getEventForPublic(eventId, request);
    }

    private void checkDateAndThrowException(LocalDateTime start, LocalDateTime end) {
        if ((start != null && end != null) && start.isAfter(end)) {
            throw new IllegalArgumentException("Start after end");
        }
    }
}
