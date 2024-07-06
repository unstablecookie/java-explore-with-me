package ru.practicum.statistics.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatistics(@RequestParam(required = true) String start,
                                            @RequestParam(required = true) String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(defaultValue = "false") boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(java.net.URLDecoder.decode(start), formatter);
        LocalDateTime endDate = LocalDateTime.parse(java.net.URLDecoder.decode(end), formatter);
        return statsService.getStatistics(startDate, endDate, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        statsService.addHit(endpointHitDto);
    }
}
