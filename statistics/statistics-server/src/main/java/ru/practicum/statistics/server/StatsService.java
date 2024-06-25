package ru.practicum.statistics.server;

import ru.practicum.statistics.dto.ViewStatsDto;
import ru.practicum.statistics.server.model.EndpointHit;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    void addHit(EndpointHit endpointHit);
}
