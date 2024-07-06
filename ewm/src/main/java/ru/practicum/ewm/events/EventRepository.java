package ru.practicum.ewm.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Event findByCategoryId(Long id);

    Page<Event> findByInitiatorId(Long userId, Pageable page);

    List<Event> findAllByIdIn(List<Long> eventIds);
}
