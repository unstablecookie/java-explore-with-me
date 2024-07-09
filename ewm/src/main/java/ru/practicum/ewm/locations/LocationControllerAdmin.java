package ru.practicum.ewm.locations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.locations.dto.LocationShort;
import ru.practicum.ewm.locations.dto.NewLocationAdminDto;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.types.LocationTypeService;
import ru.practicum.ewm.locations.types.dto.LocationTypeDto;
import ru.practicum.ewm.locations.types.dto.NewLocationTypeDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/locations")
public class LocationControllerAdmin {
    private final LocationService locationService;
    private final LocationTypeService locationTypeService;

    @PutMapping("/types")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationTypeDto addLocationType(@RequestBody @Valid NewLocationTypeDto newLocationTypeDto) {
        return locationTypeService.addLocation(newLocationTypeDto);
    }

    @GetMapping("/types")
    @ResponseStatus(HttpStatus.OK)
    public List<LocationTypeDto> getLocationTypes(@RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        return locationTypeService.getLocationTypes(from, size);
    }

    @GetMapping("/types/{typeId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationTypeDto getLocationTypeById(@PathVariable Long typeId) {
        return locationTypeService.getLocationTypeById(typeId);
    }

    @PatchMapping("/types/{typeId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationTypeDto updateLocationType(@PathVariable Long typeId,
                                              @RequestBody @Valid LocationTypeDto locationTypeDto) {
        return locationTypeService.updateLocationType(typeId, locationTypeDto);
    }

    @DeleteMapping("/types/{typeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocationType(@PathVariable Long typeId) {
        locationTypeService.deleteLocationType(typeId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto addLocation(@RequestBody @Valid NewLocationAdminDto newLocationAdminDto) {
        return locationService.addLocation(newLocationAdminDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LocationDto> getLocations(@RequestParam(required = false, defaultValue = "0") int from,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        return locationService.getLocations(from, size);
    }

    @GetMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto getLocationById(@PathVariable Long locationId) {
        return locationService.getLocationById(locationId);
    }

    @GetMapping("/nearest")
    @ResponseStatus(HttpStatus.OK)
    public List<LocationShort> getNearestLocationsInRange(@RequestParam Float lat,
                                                          @RequestParam Float lon,
                                                          @RequestParam Float radius) {
        return locationService.getNearestLocationsInRange(lat, lon, radius);
    }

    @PatchMapping("/{locationId}")
    @ResponseStatus(HttpStatus.OK)
    public LocationDto updateLocation(@PathVariable Long locationId,
                                          @RequestBody @Valid LocationDto locationDto) {
        return locationService.updateLocation(locationId, locationDto);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable Long locationId) {
        locationService.deleteLocation(locationId);
    }
}
