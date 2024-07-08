package ru.practicum.ewm.locations.types.dto;

import ru.practicum.ewm.locations.types.model.LocationType;

public class LocationTypeMapper {
    public static LocationType toLocationType(NewLocationTypeDto newLocationTypeDto) {
        return LocationType.builder()
                .name(newLocationTypeDto.getName())
                .build();
    }

    public static LocationTypeDto toLocationTypeDto(LocationType locationType) {
        return LocationTypeDto.builder()
                .name(locationType.getName())
                .build();
    }
}
