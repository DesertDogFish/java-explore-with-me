package ru.practicum.explorewithme.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ViewStatsDto {
    @NotBlank
    private final String app;
    @NotBlank
    private final String uri;
    @NotNull
    private final Long hits;
}
