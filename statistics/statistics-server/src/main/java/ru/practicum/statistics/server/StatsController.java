package ru.practicum.statistics.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistics.dto.ViewStatsDto;
import ru.practicum.statistics.server.model.EndpointHit;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistics(@RequestParam(required = true) LocalDateTime start,
                                            @RequestParam(required = true) LocalDateTime end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getStatistics(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public void addHit(@RequestBody @Valid EndpointHit endpointHit) {
        statsService.addHit(endpointHit);
    }
}
