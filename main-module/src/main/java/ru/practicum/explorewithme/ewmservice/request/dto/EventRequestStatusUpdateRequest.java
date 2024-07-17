package ru.practicum.explorewithme.ewmservice.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.explorewithme.ewmservice.request.model.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class EventRequestStatusUpdateRequest {
    @NotNull
    private final List<Long> requestIds;
    @NotNull
    private final RequestStatus status;
}
