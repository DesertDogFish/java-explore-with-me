package ru.practicum.explorewithme.ewmservice.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationDto {
    private Double lat;
    private Double lon;
    @Size(max = 250)
    private String name;
}
