package ru.practicum.explorewithme.ewmservice.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryShortDto {
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}
