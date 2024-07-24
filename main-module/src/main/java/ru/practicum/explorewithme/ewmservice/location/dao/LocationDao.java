package ru.practicum.explorewithme.ewmservice.location.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

public interface LocationDao extends JpaRepository<Location, Long> {
    Location findLocationByLatAndLon(Double lat, Double lon);

    Page<Location> findAll(Pageable pageable);
}