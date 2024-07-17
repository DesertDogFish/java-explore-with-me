package ru.practicum.explorewithme.ewmservice.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationDto;
import ru.practicum.explorewithme.ewmservice.location.mapper.LocationDtoMapper;
import ru.practicum.explorewithme.ewmservice.location.model.Location;
import ru.practicum.explorewithme.ewmservice.location.dao.LocationDao;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {
    private final LocationDao locationDao;
    private final LocationDtoMapper locationDtoMapper;

    @Override
    @Transactional
    public Location save(LocationDto locationDto) {
        Location location = locationDao.findLocationByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location == null) {
            return locationDao.save(locationDtoMapper.toModel(locationDto));
        }
        return location;
    }
}
