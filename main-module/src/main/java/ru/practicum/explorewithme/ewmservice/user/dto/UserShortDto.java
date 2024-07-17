package ru.practicum.explorewithme.ewmservice.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
    @Email
    @NotBlank
    @Size(max = 100)
    private String email;
}
