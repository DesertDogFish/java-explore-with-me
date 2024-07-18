package ru.practicum.explorewithme.ewmservice.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    @NotNull
    private Double lat;
    @NotNull
    private Double lon;
}
