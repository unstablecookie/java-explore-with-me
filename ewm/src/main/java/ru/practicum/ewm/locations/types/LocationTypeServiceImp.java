package ru.practicum.ewm.locations.types;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.error.IntegrityConflictException;
import ru.practicum.ewm.locations.LocationRepository;
import ru.practicum.ewm.locations.types.dto.LocationTypeDto;
import ru.practicum.ewm.locations.types.dto.LocationTypeMapper;
import ru.practicum.ewm.locations.types.dto.NewLocationTypeDto;
import ru.practicum.ewm.locations.types.model.LocationType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationTypeServiceImp implements LocationTypeService {
    private final LocationTypeRepository locationTypeRepository;
    private final LocationRepository locationRepository;

    @Override
    public LocationTypeDto addLocation(NewLocationTypeDto newLocationTypeDto) {
        LocationType locationType = locationTypeRepository.save(LocationTypeMapper.toLocationType(newLocationTypeDto));
        return LocationTypeMapper.toLocationTypeDto(locationType);
    }

    @Override
    public List<LocationTypeDto> getLocationTypes(int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return locationTypeRepository.findAll(page).stream()
                .map(x -> LocationTypeMapper.toLocationTypeDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public LocationTypeDto updateLocationType(Long typeId, LocationTypeDto locationTypeDto) {
        LocationType locationType = locationTypeRepository.findById(typeId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location type with id=%d was not found", typeId),
                        "The required object was not found.")
        );
        locationType.setName(locationTypeDto.getName());
        LocationType updatedLocationType = locationTypeRepository.save(locationType);
        return LocationTypeMapper.toLocationTypeDto(updatedLocationType);
    }

    @Override
    public void deleteLocationType(Long typeId) {
        LocationType locationType = locationTypeRepository.findById(typeId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location type with id=%d was not found", typeId),
                        "The required object was not found.")
        );
        if (locationRepository.findByTypeId(typeId) != null) {
            throw new IntegrityConflictException("The type is not empty",
                    "For the requested operation the conditions are not met.");
        }
        locationTypeRepository.delete(locationType);
    }

    @Override
    public LocationTypeDto getLocationTypeById(Long typeId) {
        LocationType locationType = locationTypeRepository.findById(typeId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Location type with id=%d was not found", typeId),
                        "The required object was not found.")
        );
        return LocationTypeMapper.toLocationTypeDto(locationType);
    }
}
