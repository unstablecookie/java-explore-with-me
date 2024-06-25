package ru.practicum.statistics.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statistics.server.dto.IViewStatsDto;
import ru.practicum.statistics.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT * FROM ( " +
            "SELECT distinct on (e.uri) e.app, e.uri , ee.hits from ( " +
            " SELECT uri, count(uri) as hits " +
            " FROM endpointhits WHERE timestamp >= ?1 and timestamp <= ?2 GROUP BY uri ) ee " +
            "JOIN endpointhits e on e.uri = ee.uri ) t ORDER BY t.hits DESC;", nativeQuery = true)
    List<IViewStatsDto> findEndpointHits(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * FROM ( " +
            "SELECT distinct on (ee.uri) e.app, ee.uri, ee.hits FROM ( " +
            "SELECT uri, count(distinct ip) as hits from endpointhits WHERE timestamp >= ?1 and timestamp <= ?2 " +
            "group by uri ORDER BY hits DESC " +
            ") ee JOIN endpointhits e on e.uri = ee.uri ) t ORDER BY t.hits DESC;", nativeQuery = true)
    List<IViewStatsDto> findEndpointHitsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * FROM ( " +
            "SELECT distinct on (e.uri) e.app, e.uri , ee.hits from ( " +
            " SELECT uri, count(uri) as hits " +
            " FROM endpointhits WHERE uri in ?3 and timestamp >= ?1 and timestamp <= ?2 GROUP BY uri ) ee " +
            "JOIN endpointhits e on e.uri = ee.uri ) t ORDER BY t.hits DESC;", nativeQuery = true)
    List<IViewStatsDto> findEndpointHitsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT * FROM ( " +
            "SELECT distinct on (ee.uri) e.app, ee.uri, ee.hits FROM ( " +
            "SELECT uri, count(distinct ip) as hits from endpointhits WHERE uri in ?3 and timestamp >= ?1 " +
            "and timestamp <= ?2 group by uri ORDER BY hits DESC " +
            ") ee JOIN endpointhits e on e.uri = ee.uri ) t ORDER BY t.hits DESC;", nativeQuery = true)
    List<IViewStatsDto> findEndpointHitsWithUniqueIpWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
