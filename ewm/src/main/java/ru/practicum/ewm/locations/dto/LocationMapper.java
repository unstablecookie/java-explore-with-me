package ru.practicum.ewm.locations.dto;

import ru.practicum.ewm.locations.model.Location;

public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .radius(location.getRadius() != null ? location.getRadius() : null)
                .name(location.getName() != null ? location.getName() : null)
                .type(location.getType() != null ? location.getType().getId() : null)
                .build();
    }

    public static Location toLocation(NewLocationAdminDto newLocationAdminDto) {
        return Location.builder()
                .lat(newLocationAdminDto.getLat())
                .lon(newLocationAdminDto.getLon())
                .radius(newLocationAdminDto.getRadius())
                .name(newLocationAdminDto.getName())
                .build();
    }

    public static Location updateLocation(LocationDto locationDto, Location location) {
        return Location.builder()
                .id(location.getId())
                .lat(locationDto.getLat() != null ? locationDto.getLat() : location.getLat())
                .lon(locationDto.getLon() != null ? locationDto.getLon() : location.getLon())
                .radius(locationDto.getRadius() != null ? locationDto.getRadius() : location.getRadius())
                .name(locationDto.getName() != null ? locationDto.getName() : location.getName())
                .type(location.getType() != null ? location.getType() : null)
                .build();
    }
}
