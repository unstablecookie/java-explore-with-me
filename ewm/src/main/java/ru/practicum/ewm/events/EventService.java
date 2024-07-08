package ru.practicum.ewm.events;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.model.SortType;
import ru.practicum.ewm.events.model.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sortType,
                                       int from, int size, HttpServletRequest request);

    List<EventFullDto> getEventsAdmin(List<Long> users, State states, List<Long> categories, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, int from, int size, HttpServletRequest request);

    EventFullDto getEventPublic(Long id, HttpServletRequest request);

    EventFullDto updateEvent(UpdateEventAdminRequest updateEventAdminRequest, Long eventId);

    EventFullDto addUserEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateEventByUser(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId);

    List<EventFullDto> getEventsAdminByLocation(Long locationId, Float rangeKm);
}
