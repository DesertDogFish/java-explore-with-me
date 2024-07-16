package ru.practicum.explorewithme.statsservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.statsservice.dao.StatsDao;
import ru.practicum.explorewithme.statsservice.mapper.EndpointHitDtoMapper;
import ru.practicum.explorewithme.statsservice.mapper.ViewStatsDtoMapper;
import ru.practicum.explorewithme.statsservice.model.StatisticData;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsDao statsDao;

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique)
            return ViewStatsDtoMapper.toDtoList(statsDao.findViewStatsUnique(start, end, uris));
        else
            return ViewStatsDtoMapper.toDtoList(statsDao.findViewStats(start, end, uris));
    }

    @Override
    @Transactional
    public EndpointHitDto post(EndpointHitDto endpointHitDto) {
        StatisticData data = statsDao.save(EndpointHitDtoMapper.toModel(endpointHitDto));
        return EndpointHitDtoMapper.toDto(data);
    }
}