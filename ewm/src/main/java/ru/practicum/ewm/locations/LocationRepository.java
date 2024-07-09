package ru.practicum.ewm.locations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.locations.dto.IHolder;
import ru.practicum.ewm.locations.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByTypeId(Long typeId);

    @Query(value = "SELECT distance(?1, ?2, ?3) as holders", nativeQuery = true)
    IHolder getNearestLocationsInRange(Float lat, Float lon, Float radius);

    Page<Location> findAll(Pageable page);
}
