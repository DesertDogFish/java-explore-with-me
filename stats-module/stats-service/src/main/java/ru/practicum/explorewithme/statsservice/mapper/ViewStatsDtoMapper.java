package ru.practicum.explorewithme.statsservice.mapper;

import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.statsservice.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

public class ViewStatsDtoMapper {
    public static ViewStatsDto toDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .hits(viewStats.getHits())
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .build();
    }

    public static List<ViewStatsDto> toDtoList(List<ViewStats> viewStats) {
        return viewStats.stream().map(ViewStatsDtoMapper::toDto).collect(Collectors.toList());
    }
}
