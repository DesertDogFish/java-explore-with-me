package ru.practicum.explorewithme.ewmservice.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationFullDto {
    private Long id;
    private Double lat;
    private Double lon;
    private String name;
}
