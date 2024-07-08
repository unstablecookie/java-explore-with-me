package ru.practicum.ewm.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationMapper;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.model.Event;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImp implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = newCompilationDto.getEvents() != null ? eventRepository.findAllByIdIn(newCompilationDto.getEvents()) :
                List.of();
        Compilation compilation = Compilation.builder()
                .pinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : Boolean.FALSE)
                .title(newCompilationDto.getTitle())
                .events(new HashSet<>(events))
                .build();
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId),
                        "The required object was not found.")
        );
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId),
                        "The required object was not found.")
        );
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }
        compilation.setTitle(updateCompilationRequest.getTitle() != null ? updateCompilationRequest.getTitle() :
                compilation.getTitle());
        compilation.setPinned(updateCompilationRequest.getPinned() != null ? updateCompilationRequest.getPinned() :
                compilation.getPinned());
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (pinned) {
            return compilationRepository.findByPinned(Boolean.valueOf(pinned), page).stream()
                    .map(x -> CompilationMapper.toCompilationDto(x))
                    .collect(Collectors.toList());
        } else {
            return  compilationRepository.findAll(page).stream()
                    .map(x -> CompilationMapper.toCompilationDto(x))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId),
                        "The required object was not found.")
        );
        return CompilationMapper.toCompilationDto(compilation);
    }
}
