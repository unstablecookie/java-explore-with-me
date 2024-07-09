package ru.practicum.ewm.locations;

import ru.practicum.ewm.locations.dto.LocationShort;
import ru.practicum.ewm.locations.dto.NewLocationAdminDto;
import ru.practicum.ewm.locations.dto.LocationDto;
import java.util.List;

public interface LocationService {
    LocationDto addLocation(NewLocationAdminDto newLocationAdminDto);

    LocationDto updateLocation(Long locationId, LocationDto locationDto);

    void deleteLocation(Long locationId);

    List<LocationShort> getNearestLocationsInRange(Float lat, Float lon, Float radius);

    List<LocationDto> getLocations(int from, int size);

    LocationDto getLocationById(Long locationId);
}
