package ru.practicum.explorewithme.ewmservice.location.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationFullDto;
import ru.practicum.explorewithme.ewmservice.location.dto.NewLocationDto;
import ru.practicum.explorewithme.ewmservice.location.dto.UpdateLocationDto;
import ru.practicum.explorewithme.ewmservice.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/locations")
public class LocationAdminController {
    private final LocationService locationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto save(@RequestBody @Valid NewLocationDto dto) {
        return locationService.save(dto);
    }

    @PatchMapping("/{locationId}")
    public LocationFullDto patch(@PathVariable Long locationId,
                                 @Valid @RequestBody UpdateLocationDto dto) {
        return locationService.patch(locationId, dto);
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long locationId) {
        locationService.delete(locationId);
    }

    @GetMapping("/{locationId}")
    public LocationFullDto get(@PathVariable Long locationId) {
        return locationService.get(locationId);
    }

    @GetMapping()
    public List<LocationFullDto> getAll(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return locationService.getAll(from, size);
    }

    @GetMapping("/find")
    public List<LocationFullDto> find(
            @RequestParam() Double lat,
            @RequestParam() Double lon,
            @RequestParam(required = false, defaultValue = "100") Double radius) {
        return locationService.find(lat, lon, radius);
    }
}
