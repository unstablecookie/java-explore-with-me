package ru.practicum.ewm.locations.types;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.locations.types.model.LocationType;

public interface LocationTypeRepository extends JpaRepository<LocationType, Long> {
    Page<LocationType> findAll(Pageable page);
}
