package ru.practicum.explorewithme.ewmservice.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewmservice.common.DistanceCalculator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<LocationFullDto> find(Double lat, Double lon, Double radius, Integer pageSize) {
        int from = 0;
        int batchSize = 100;
        List<Location> locations = new ArrayList<>();
        while (true) {
            List<Location> locationsPage = locationDao.findAll(new PageRequestHelper(from, batchSize, null)).toList();
            if (locationsPage.isEmpty()) {
                break;
            }
            locationsPage = filterLocationsByRadius(locationsPage, lat, lon, radius);
            locations.addAll(locationsPage);
            if (locations.size() >= pageSize) {
                break;
            }
            from += batchSize;
        }
        return locationFullDtoMapper.toDtoList(locations.subList(0, Math.min(pageSize, locations.size())));
    }

    private List<Location> filterLocationsByRadius(List<Location> locations, Double lat, Double lon, Double radius) {
        return locations.stream()
                .filter(location -> DistanceCalculator.calculateDistance(lat, lon, location.getLat(), location.getLon()) <= radius)
                .collect(Collectors.toList());
    }

    private Location getLocationOrThrowNotFoundException(Long id) {
        return locationDao.findById(id)
                .orElseThrow(() -> new NotFoundException("No such location"));
    }
}
