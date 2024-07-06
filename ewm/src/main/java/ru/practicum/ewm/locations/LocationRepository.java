package ru.practicum.ewm.locations;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.locations.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
