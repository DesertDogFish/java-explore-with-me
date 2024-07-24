package ru.practicum.explorewithme.ewmservice.location.service;

import ru.practicum.explorewithme.ewmservice.location.dto.LocationDto;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationFullDto;
import ru.practicum.explorewithme.ewmservice.location.dto.NewLocationDto;
import ru.practicum.explorewithme.ewmservice.location.dto.UpdateLocationDto;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

import java.util.List;

public interface LocationService {

    Location save(LocationDto locationDto);

    LocationFullDto save(NewLocationDto newLocationDto);

    LocationFullDto patch(Long locationId, UpdateLocationDto newLocationDto);

    void delete(Long locationId);

    LocationFullDto get(Long locationId);

    List<LocationFullDto> getAll(Integer from, Integer size);

    List<LocationFullDto> find(
            Double lat, Double lon, Double radius, Integer pageSize);
}
