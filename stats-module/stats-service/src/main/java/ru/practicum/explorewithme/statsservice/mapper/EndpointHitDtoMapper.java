package ru.practicum.explorewithme.statsservice.mapper;

import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.statsservice.model.StatisticData;

public class EndpointHitDtoMapper {
    public static StatisticData toModel(EndpointHitDto endpointHitDto) {
        return StatisticData.builder()
                .id(endpointHitDto.getId())
                .uri(endpointHitDto.getUri())
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static EndpointHitDto toDto(StatisticData statisticData) {
        return EndpointHitDto.builder()
                .id(statisticData.getId())
                .uri(statisticData.getUri())
                .app(statisticData.getApp())
                .ip(statisticData.getIp())
                .timestamp(statisticData.getTimestamp())
                .build();
    }
}
