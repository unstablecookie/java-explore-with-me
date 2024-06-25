package ru.practicum.statistics.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statistics.server.dto.IViewStatsDto;
import ru.practicum.statistics.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT distinct on (e.uri) e.app, e.uri , ee.hits from ( " +
            " SELECT app, count(app) as hits " +
            " FROM endpointhits WHERE timestamp >= ?1 and timestamp <= ?2 GROUP BY app ) ee " +
            "JOIN endpointhits e on e.app = ee.app", nativeQuery = true)
    List<IViewStatsDto> findEndpointHits(LocalDateTime start, LocalDateTime end);

    @Query(value = "select distinct on (e.uri) e.app, e.uri, ee.hits from ( " +
            "select app, count(app) as hits from ( " +
            "select distinct on (ip) app from endpointhits WHERE timestamp >= ?1 and timestamp <= ?2 ) ips " +
            "group by app ) ee join endpointhits e on e.app = ee.app",
            nativeQuery = true)
    List<IViewStatsDto> findEndpointHitsWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT distinct on (e.uri) e.app, e.uri , ee.hits from ( " +
            " SELECT app, count(app) as hits " +
            " FROM endpointhits WHERE uri in ?3 and timestamp >= ?1 and timestamp <= ?2 GROUP BY app ) ee " +
            "JOIN endpointhits e on e.app = ee.app WHERE uri in ?3", nativeQuery = true)
    List<IViewStatsDto> findEndpointHitsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select distinct on (e.uri) e.app, e.uri, ee.hits from ( " +
            "select app, count(app) as hits from ( " +
            "select distinct on (ip) app from endpointhits WHERE uri in ?3 and timestamp >= ?1 and timestamp <= ?2 ) ips " +
            "group by app ) ee join endpointhits e on e.app = ee.app  WHERE uri in ?3",
            nativeQuery = true)
    List<IViewStatsDto> findEndpointHitsWithUniqueIpWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
