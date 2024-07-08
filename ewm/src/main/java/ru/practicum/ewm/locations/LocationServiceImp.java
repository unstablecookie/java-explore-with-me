package ru.practicum.ewm.locations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.error.IntegrityConflictException;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.locations.dto.*;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.locations.types.LocationTypeRepository;
import ru.practicum.ewm.locations.types.model.LocationType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImp implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationTypeRepository locationTypeRepository;
    private final EventRepository eventRepository;

    @Override
    public LocationDto addLocation(NewLocationAdminDto newLocationAdminDto) {
        Location location = LocationMapper.toLocation(newLocationAdminDto);
        if (newLocationAdminDto.getType() != null) {
            LocationType locationType = locationTypeRepository.findById(newLocationAdminDto.getType()).orElseThrow(
                    () -> new EntityNotFoundException(String.format("Location type with id=%d was not found",
                            newLocationAdminDto.getType()), "The required object was not found.")
            );
            location.setType(locationType);
        }
        locationRepository.save(location);
        return LocationMapper.toLocationDto(location);
    }

    @Override
    public List<LocationDto> getLocations(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return locationRepository.findAll(page).stream()
                .map(x -> LocationMapper.toLocationDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public LocationDto updateLocation(Long locationId, LocationDto locationDto) {
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location with id=%d was not found", locationId),
                        "The required object was not found.")
        );
        location = LocationMapper.updateLocation(locationDto, location);
        if (locationDto.getType() != null) {
            LocationType locationType = locationTypeRepository.findById(locationDto.getType()).orElseThrow(
                    () -> new EntityNotFoundException(String.format("Location type with id=%d was not found", locationDto.getType()),
                            "The required object was not found.")
            );
            location.setType(locationType);
        }
        Location updatedLocation = locationRepository.save(location);
        return LocationMapper.toLocationDto(location);
    }

    @Override
    public void deleteLocation(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location with id=%d was not found", locationId),
                        "The required object was not found.")
        );
        if (eventRepository.findByLocationId(locationId) != null) {
            throw new IntegrityConflictException("The location is not empty",
                    "For the requested operation the conditions are not met.");
        }
        locationRepository.delete(location);
    }

    @Override
    public List<LocationShort> getNearestLocationsInRange(Float lat, Float lon, Float radius) {
        List<LocationShort> locationShorts = new ArrayList<>();
        IHolder locations = locationRepository.getNearestLocationsInRange(lat, lon, radius);
        for (Object o : locations.getHolders()) {
            String[] tuple = o.toString().substring(1,o.toString().length() - 1).split(",");
            locationShorts.add(LocationShort.builder()
                            .id(Long.valueOf(tuple[0]))
                            .dist(Float.valueOf(tuple[1]))
                    .build());
        }
        return locationShorts;
    }

    @Override
    public LocationDto getLocationById(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location with id=%d was not found", locationId),
                        "The required object was not found.")
        );
        return LocationMapper.toLocationDto(location);
    }
}
