package ru.practicum.statistics.server;

import ru.practicum.statistics.dto.ViewStatsDto;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import ru.practicum.statistics.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImp implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris != null) {
            return prepareUniqueStatisticsWithUris(start, end, uris, unique);
        }
        return prepareUniqueStatistics(start, end, unique);
    }

    @Override
    public void addHit(EndpointHit endpointHit) {
        statsRepository.save(endpointHit);
    }

    private List<ViewStatsDto> prepareUniqueStatistics(LocalDateTime start, LocalDateTime end, boolean unique) {
        if (unique) {
            return statsRepository.findEndpointHitsWithUniqueIp(start, end).stream()
                    .map(x -> new ViewStatsDto(x.getApp(), x.getUri(), x.getHits())).collect(Collectors.toList());
        }
        return statsRepository.findEndpointHits(start, end).stream()
                .map(x -> new ViewStatsDto(x.getApp(), x.getUri(), x.getHits())).collect(Collectors.toList());
    }

    private List<ViewStatsDto> prepareUniqueStatisticsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris,
                                                               boolean unique) {
        if (unique) {
            return statsRepository.findEndpointHitsWithUniqueIpWithUris(start, end, uris).stream()
                    .map(x -> new ViewStatsDto(x.getApp(), x.getUri(), x.getHits())).collect(Collectors.toList());
        }
        return statsRepository.findEndpointHitsWithUris(start, end, uris).stream()
                .map(x -> new ViewStatsDto(x.getApp(), x.getUri(), x.getHits())).collect(Collectors.toList());
    }
}
