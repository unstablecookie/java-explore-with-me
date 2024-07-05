package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.categories.dto.CategoryMapper;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.locations.dto.LocationMapper;
import ru.practicum.ewm.users.dto.UserMapper;
import ru.practicum.ewm.users.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class EventMapper {
    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation() != null ? event.getAnnotation() : null)
                .category(event.getCategory() != null ? CategoryMapper.toCategoryDto(event.getCategory()) : null)
                .confirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : null)
                .eventDate(event.getEventDate() != null ? event.getEventDate() : null)
                .id(event.getId() != null ? event.getId() : null)
                .initiator(event.getInitiator() != null ? UserMapper.toUserShortDto(event.getInitiator()) : null)
                .paid(event.getPaid() != null ? event.getPaid() : null)
                .title(event.getTitle() != null ? event.getTitle() : null)
                .views(event.getViews() != null ? event.getViews() : null)
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto, User user, Category category) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation() != null ? newEventDto.getAnnotation() : null)
                .category(category)
                .createdOn(LocalDateTime.now(ZoneOffset.UTC))
                .description(newEventDto.getDescription() != null ? newEventDto.getDescription() : null)
                .eventDate(newEventDto.getEventDate() != null ? newEventDto.getEventDate() : null)
                .initiator(user)
                .location(newEventDto.getLocation() != null ? newEventDto.getLocation() : null)
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : Boolean.FALSE)
                .participantLimit(Long.valueOf(newEventDto.getParticipantLimit()))
                .requestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : Boolean.TRUE)
                .title(newEventDto.getTitle() != null ? newEventDto.getTitle() : null)
                .build();
    }
}
