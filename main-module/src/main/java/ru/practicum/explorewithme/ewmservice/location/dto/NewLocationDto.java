package ru.practicum.explorewithme.ewmservice.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewLocationDto {
    @NotNull
    private Double lat;
    @NotNull
    private Double lon;
    @Size(max = 250)
    private String name;
}
