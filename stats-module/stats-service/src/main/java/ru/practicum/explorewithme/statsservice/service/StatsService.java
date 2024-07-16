package ru.practicum.explorewithme.statsservice.service;

import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    EndpointHitDto post(EndpointHitDto endpointHitDto);
}
