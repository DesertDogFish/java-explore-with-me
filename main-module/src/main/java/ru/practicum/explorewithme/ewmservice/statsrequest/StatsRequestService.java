package ru.practicum.explorewithme.ewmservice.statsrequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.EndpointHitDto;
import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.statsclient.StatsServiceClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class StatsRequestService {
    private final ObjectMapper objectMapper;
    private final StatsServiceClient statsServiceClient;
    public List<ViewStatsDto> getViewStatsDtoList(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<String> uris, boolean unique) {
        ResponseEntity<Object> stats = statsServiceClient.stats(rangeStart, rangeEnd, uris, unique);
        List<ViewStatsDto> statisticDtos;
        if (stats.getStatusCode().is2xxSuccessful()) {
            statisticDtos = objectMapper.convertValue(stats.getBody(), new TypeReference<>() {
            });
        } else {
            throw new RuntimeException(Objects.requireNonNull(stats.getBody()).toString());
        }
        return statisticDtos;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        statsServiceClient.hit(endpointHitDto);
    }
}
