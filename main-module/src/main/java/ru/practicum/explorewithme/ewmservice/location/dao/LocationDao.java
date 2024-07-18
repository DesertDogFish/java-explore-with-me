package ru.practicum.explorewithme.ewmservice.location.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.ewmservice.location.model.Location;

public interface LocationDao extends JpaRepository<Location, Long> {
    Location findLocationByLatAndLon(Double lat, Double lon);
}