package ru.practicum.ewm.compilations.dto;

import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.dto.EventMapper;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .events(compilation.getEvents().stream()
                        .map(x -> EventMapper.toEventShortDto(x))
                        .collect(Collectors.toSet()))
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
