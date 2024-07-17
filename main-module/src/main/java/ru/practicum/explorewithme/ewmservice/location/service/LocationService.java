package ru.practicum.explorewithme.ewmservice.location.service;

import ru.practicum.explorewithme.ewmservice.location.dto.LocationDto;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

public interface LocationService {
    Location save(LocationDto locationDto);
}
