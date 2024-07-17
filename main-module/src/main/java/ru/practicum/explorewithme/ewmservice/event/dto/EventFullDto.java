package ru.practicum.explorewithme.ewmservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.ewmservice.category.dto.CategoryDto;
import ru.practicum.explorewithme.ewmservice.event.model.enums.EventState;
import ru.practicum.explorewithme.ewmservice.location.dto.LocationDto;
import ru.practicum.explorewithme.ewmservice.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.explorewithme.ewmservice.common.DateTimeUtil.DATE_TIME_FORMAT;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;
}
