package ru.practicum.explorewithme.ewmservice.request.service;


import ru.practicum.explorewithme.ewmservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.ewmservice.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    ParticipationRequestDto save(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllForRequester(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestsId);

    List<ParticipationRequestDto> get(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            EventRequestStatusUpdateRequest statusUpdateRequestDto, Long userId, Long eventId);

    Map<Long, Integer> countConfirmedRequests(List<Long> eventIds);
}
