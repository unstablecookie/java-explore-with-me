package ru.practicum.ewm.locations.types;

import ru.practicum.ewm.locations.types.dto.LocationTypeDto;
import ru.practicum.ewm.locations.types.dto.NewLocationTypeDto;

import java.util.List;

public interface LocationTypeService {

    LocationTypeDto addLocation(NewLocationTypeDto newLocationTypeDto);

    LocationTypeDto updateLocationType(Long typeId, LocationTypeDto locationTypeDto);

    void deleteLocationType(Long typeId);

    List<LocationTypeDto> getLocationTypes(int from, int size);

    LocationTypeDto getLocationTypeById(Long typeId);
}
