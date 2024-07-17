package ru.practicum.explorewithme.statsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.statsservice.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsServiceController {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> get(@RequestParam(name = "start") @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime start,
                                  @RequestParam(name = "end") @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        validateDates(start, end);
        return statsService.get(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto hit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return statsService.post(endpointHitDto);
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date should be after end date");
        }
    }
}
