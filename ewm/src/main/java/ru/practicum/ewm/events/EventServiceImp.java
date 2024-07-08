package ru.practicum.ewm.events;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.error.EventStatusException;
import ru.practicum.ewm.error.InputParametersException;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.*;
import ru.practicum.ewm.locations.LocationRepository;
import ru.practicum.ewm.locations.dto.IHolder;
import ru.practicum.ewm.locations.dto.LocationShort;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.users.UserRepository;
import ru.practicum.ewm.users.model.User;
import ru.practicum.statistics.client.StatisticsClient;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImp implements EventService {
    @Value("${client.url}")
    private String clientConnectionUrl;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final StatisticsClient client = new StatisticsClient();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> getEventsAdmin(List<Long> users, State states, List<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, int from, int size, HttpServletRequest request) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Specification<Event> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (users != null && users.size() > 0) {
                predicates.add(builder.and(root.get("initiator").in(users)));
            }
            if (states != null) {
                predicates.add(builder.and(root.get("state").in(states)));
            }
            if (categories != null && categories.size() > 0) {
                predicates.add(builder.and(root.get("category").in(categories)));
            }
            if (rangeStart != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeStart));
            }
            if (rangeEnd != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeEnd));
            }
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Event> events = eventRepository.findAll(specification, page).stream()
                .collect(Collectors.toList());
        return events.stream()
                .map(x -> EventMapper.toEventFullDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable, SortType sortType,
                                              int from, int size, HttpServletRequest request) {
        PageRequest page = (sortType != null) ? createPageRequestWithSort(from, size, sortType) : PageRequest.of(from > 0 ? from / size : 0, size);
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart) || rangeStart.equals(rangeEnd)) {
                throw new InputParametersException("end date cannot be before start date.", "Incorrectly made request.");
            }
        }
        LocalDateTime start = (rangeStart == null && rangeEnd == null) ? LocalDateTime.now() : rangeStart;
        Specification<Event> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (text != null) {
                predicates.add(builder.or(
                        builder.like(
                                builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                        builder.like(
                                builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                ));
            }
            if (categories != null && categories.size() > 0) {
                predicates.add(builder.and(root.get("category").in(categories)));
            }
            if (paid != null) {
                if (paid.booleanValue()) {
                    predicates.add(builder.isTrue(root.get("paid")));
                } else {
                    predicates.add(builder.isFalse(root.get("paid")));
                }
            }
            if (start != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));
            }
            if (rangeEnd != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeEnd));
            }
            predicates.add(builder.equal(root.get("state").as(String.class), State.PUBLISHED.toString()));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        List<Event> events = eventRepository.findAll(specification, page).stream()
                .collect(Collectors.toList());
        saveStatistics(request);
        return events.stream()
                .map(x -> EventMapper.toEventFullDto(x))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventAdminRequest updateEventAdminRequest, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId),
                        "The required object was not found.")
        );
        if (updateEventAdminRequest == null) {
            return EventMapper.toEventFullDto(event);
        }
        if (event.getEventDate().isBefore(LocalDateTime.now(ZoneOffset.UTC).plusHours(1))) {
            throw new EventStatusException(String.format("Cannot publish the event because of time restrictions",event.getState()),
                    "For the requested operation the conditions are not met.");
        }
        if (updateEventAdminRequest.getEventDate() != null && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now(ZoneOffset.UTC).plusHours(1))) {
            throw new EventStatusException(String.format("Cannot publish the event because of time restrictions",event.getState()),
                    "For the requested operation the conditions are not met.");
        }
        event.setAnnotation(updateEventAdminRequest.getAnnotation() != null ? updateEventAdminRequest.getAnnotation() : event.getAnnotation());
        if (updateEventAdminRequest.getCategory() != null) {
            Long categoryId = updateEventAdminRequest.getCategory();
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new EntityNotFoundException(String.format("Category with id=%d was not found", categoryId),
                            "The required object was not found.")
            );
            event.setCategory(category);
        }
        event.setDescription(updateEventAdminRequest.getDescription() != null ? updateEventAdminRequest.getDescription() :
                event.getDescription());
        event.setLocation(updateEventAdminRequest.getLocation() != null ? updateEventAdminRequest.getLocation() :
                event.getLocation());
        event.setPaid(updateEventAdminRequest.getPaid() != null ? updateEventAdminRequest.getPaid() :
                event.getPaid());
        event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit() != null ? updateEventAdminRequest.getParticipantLimit() :
                event.getParticipantLimit());
        event.setRequestModeration(updateEventAdminRequest.getRequestModeration() != null ? updateEventAdminRequest.getRequestModeration() :
                event.getRequestModeration());
        event.setTitle(updateEventAdminRequest.getTitle() != null ? updateEventAdminRequest.getTitle() :
                event.getTitle());
        if (updateEventAdminRequest.getStateAction() != null) {
            AdminState aState = updateEventAdminRequest.getStateAction();
            if (aState.equals(AdminState.PUBLISH_EVENT)) {
                if (event.getPublishedOn() != null || event.getState().equals(State.CANCELED)) {
                    throw new EventStatusException(String.format("Cannot publish the event because it's not in " +
                            "the right state: %s",event.getState()),"For the requested operation the conditions are not met.");
                }
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now(ZoneOffset.UTC));
            } else if (aState.equals(AdminState.REJECT_EVENT)) {
                if (event.getPublishedOn() != null) {
                    throw new EventStatusException(String.format("Cannot publish the event because it's not in " +
                            "the right state: %s",event.getState()),"For the requested operation the conditions are not met.");
                }
                event.setState(State.CANCELED);
            }
        }
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId),
                        "The required object was not found.")
        );
        if (updateEventUserRequest == null) {
            return EventMapper.toEventFullDto(event);
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new EventStatusException("Only pending or canceled events can be changed",
                    "For the requested operation the conditions are not met.");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now(ZoneOffset.UTC).plusHours(2))) {
            throw new EventStatusException(String.format("Cannot publish the event because of time restrictions",event.getState()),
                    "For the requested operation the conditions are not met.");
        }
        event.setAnnotation(updateEventUserRequest.getAnnotation() != null ? updateEventUserRequest.getAnnotation() : event.getAnnotation());
        if (updateEventUserRequest.getCategory() != null) {
            Long categoryId = updateEventUserRequest.getCategory();
            Category category = categoryRepository.findById(categoryId).orElseThrow(
                    () -> new EntityNotFoundException(String.format("Category with id=%d was not found", categoryId),
                            "The required object was not found.")
            );
            event.setCategory(category);
        }
        event.setDescription(updateEventUserRequest.getDescription() != null ? updateEventUserRequest.getDescription() :
                event.getDescription());
        event.setLocation(updateEventUserRequest.getLocation() != null ? updateEventUserRequest.getLocation() :
                event.getLocation());
        event.setPaid(updateEventUserRequest.getPaid() != null ? updateEventUserRequest.getPaid() :
                event.getPaid());
        event.setParticipantLimit(updateEventUserRequest.getParticipantLimit() != null ? updateEventUserRequest.getParticipantLimit() :
                event.getParticipantLimit());
        event.setRequestModeration(updateEventUserRequest.getRequestModeration() != null ? updateEventUserRequest.getRequestModeration() :
                event.getRequestModeration());
        event.setTitle(updateEventUserRequest.getTitle() != null ? updateEventUserRequest.getTitle() :
                event.getTitle());
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            } else {
                event.setState(State.CANCELED);
            }
        }
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventPublic(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", id),
                        "The required object was not found.")
        );
        if (event.getPublishedOn() == null) {
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", id),
                    "The required object was not found.");
        }
        updateViews(event, request);
        saveStatistics(request);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(eventRepository.save(savedEvent));
    }

    @Override
    @Transactional
    public EventFullDto addUserEvent(NewEventDto newEventDto, Long userId) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now(ZoneOffset.UTC).plusHours(2))) {
            throw new EventStatusException(String.format("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: ", newEventDto.getEventDate()),
                    "For the requested operation the conditions are not met.");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        "The required object was not found.")
        );
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Category with id=%d was not found", newEventDto.getCategory()),
                        "The required object was not found.")
        );
        Location location = locationRepository.save(newEventDto.getLocation());
        Event event = EventMapper.toEvent(newEventDto, user, category);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        event.setState(State.PENDING);
        Event savedEvent = eventRepository.save(event);
        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        "The required object was not found.")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId),
                        "The required object was not found.")
        );
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsAdminByLocation(Long locationId, Float rangeKm) {
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location with id=%d was not found", locationId),
                        "The required object was not found.")
        );
        List<LocationShort> locationShorts = new ArrayList<>();
        IHolder locations = locationRepository.getNearestLocationsInRange(location.getLat(), location.getLon(), rangeKm);
        for (Object o : locations.getHolders()) {
            String[] tuple = o.toString().substring(1,o.toString().length() - 1).split(",");
            locationShorts.add(LocationShort.builder()
                    .id(Long.valueOf(tuple[0]))
                    .dist(Float.valueOf(tuple[1]))
                    .build());
        }
        List<Long> locationIds = locationShorts.stream()
                .map(x -> x.getId())
                .collect(Collectors.toList());
        return eventRepository.findAllByLocationIdIn(locationIds).stream()
                .map(x -> EventMapper.toEventFullDto(x))
                .collect(Collectors.toList());
    }

    private void saveStatistics(HttpServletRequest request) {
        client.url = clientConnectionUrl;
        String timestamp = LocalDateTime.now(ZoneOffset.UTC).format(formatter);
        client.addStatistics(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(timestamp).build());
    }

    private void updateViews(Event event, HttpServletRequest request) {
        client.url = clientConnectionUrl;
        String encodedStart = java.net.URLEncoder.encode(event.getCreatedOn().format(formatter));
        String encodedEnd = java.net.URLEncoder.encode(LocalDateTime.now(ZoneOffset.UTC).format(formatter));
        List<ViewStatsDto> stats = client.getStatistics(encodedStart, encodedEnd, List.of("/events/" + event.getId()),
                Boolean.TRUE);
        if (stats.size() == 0) {
            event.setViews(1L);
            return;
        } else {
            event.setViews(Long.valueOf(stats.size()));
        }
    }

    private PageRequest createPageRequestWithSort(int from, int size, SortType sortType) {
        if (sortType.equals(SortType.EVENT_DATE)) {
            return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("eventDate"));
        } else {
            return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("views"));
        }
    }
}
