package ru.practicum.explorewithme.statsservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.dto.ViewStatsDto;
import ru.practicum.explorewithme.statsservice.model.StatisticData;
import ru.practicum.explorewithme.statsservice.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsDao extends JpaRepository<StatisticData, Long> {
    @Query(" SELECT new ru.practicum.explorewithme.statsservice.model.ViewStats(st.app, st.uri, COUNT(DISTINCT st.ip)) " +
            "FROM StatisticData st " +
            "WHERE st.timestamp BETWEEN ?1 AND ?2 " +
            "AND (st.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(DISTINCT st.ip) DESC ")
    List<ViewStats> findViewStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);


    @Query(" SELECT new ru.practicum.explorewithme.statsservice.model.ViewStats(st.app, st.uri, COUNT(st.ip)) " +
            "FROM StatisticData st " +
            "WHERE st.timestamp BETWEEN ?1 AND ?2 " +
            "AND (st.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY st.app, st.uri " +
            "ORDER BY COUNT(st.ip) DESC ")
    List<ViewStats> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
