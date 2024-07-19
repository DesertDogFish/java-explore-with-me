package ru.practicum.explorewithme.ewmservice.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewmservice.common.PageRequestHelper;
import ru.practicum.explorewithme.ewmservice.exception.NotFoundException;
import ru.practicum.explorewithme.ewmservice.location.dao.LocationDao;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationDto;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationFullDto;
import ru.practicum.explorewithme.ewmservice.location.dto.NewLocationDto;
import ru.practicum.explorewithme.ewmservice.location.dto.UpdateLocationDto;
import ru.practicum.explorewithme.ewmservice.location.mapper.LocationDtoMapper;
import ru.practicum.explorewithme.ewmservice.location.mapper.LocationFullDtoMapper;
import ru.practicum.explorewithme.ewmservice.location.mapper.NewLocationDtoMapper;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {
    private final LocationDao locationDao;
    private final LocationDtoMapper locationDtoMapper;
    private final NewLocationDtoMapper newLocationDtoMapper;
    private final LocationFullDtoMapper locationFullDtoMapper;

    @Override
    @Transactional
    public Location save(LocationDto dto) {
        Location location = locationDao.findLocationByLatAndLon(dto.getLat(), dto.getLon());
        if (location == null) {
            return locationDao.save(locationDtoMapper.toModel(dto));
        }
        return location;
    }

    @Override
    @Transactional
    public LocationFullDto save(NewLocationDto dto) {
        Location location = newLocationDtoMapper.toModel(dto);
        return locationFullDtoMapper.toDtoList(locationDao.save(location));
    }

    @Override
    @Transactional
    public LocationFullDto patch(Long id, UpdateLocationDto dto) {
        Location location = getLocationOrThrowNotFoundException(id);
        if (dto.getLat() != null) {
            location.setLat(dto.getLat());
        }
        if (dto.getLon() != null) {
            location.setLon(dto.getLon());
        }
        if (dto.getName() != null) {
            location.setName(dto.getName());
        }
        return locationFullDtoMapper.toDtoList(location);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!locationDao.existsById(id)) {
            throw new NotFoundException("No such location");
        }
        locationDao.deleteById(id);
    }

    @Override
    public LocationFullDto get(Long id) {
        Location location = getLocationOrThrowNotFoundException(id);
        return locationFullDtoMapper.toDtoList(location);
    }

    @Override
    public List<LocationFullDto> getAll(Integer from, Integer size) {
        List<Location> locations = locationDao.findAll(new PageRequestHelper(from, size, null)).toList();
        return locationFullDtoMapper.toDtoList(locations);
    }

    @Override
    public List<LocationFullDto> find(Double lat, Double lon, Double radius) {
        List<Location> locations = locationDao.findLocationByLatAndLonAndRadius(lat, lon, radius);
        return locationFullDtoMapper.toDtoList(locations);
    }

    private Location getLocationOrThrowNotFoundException(Long id) {
        return locationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("No such location"));
    }
}
