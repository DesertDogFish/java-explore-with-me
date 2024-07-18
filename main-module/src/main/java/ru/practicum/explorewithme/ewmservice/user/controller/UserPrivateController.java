package ru.practicum.explorewithme.ewmservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.event.dto.EventFullDto;
import ru.practicum.explorewithme.ewmservice.event.dto.EventShortDto;
import ru.practicum.explorewithme.ewmservice.event.dto.NewEventDto;
import ru.practicum.explorewithme.ewmservice.event.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.ewmservice.event.service.EventService;
import ru.practicum.explorewithme.ewmservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.ewmservice.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllEventsByOwner(@PathVariable Long userId,
                                                   @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getAllEventsByOwner(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@RequestBody @Valid NewEventDto newEventDto, @PathVariable Long userId) {
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventForOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventForOwner(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForRequester(@PathVariable Long userId) {
        return requestService.getAllForRequester(userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto patchEventByUser(@Valid @RequestBody UpdateEventUserRequest eventUserRequestDto,
                                         @PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return eventService.patchEventByUser(userId, eventId, eventUserRequestDto);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto save(@RequestParam Long eventId,
                                        @PathVariable Long userId) {
        return requestService.save(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestsId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestsId) {
        return requestService.cancel(userId, requestsId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForEventOwner(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        return requestService.get(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatusRequest(@RequestBody @Valid EventRequestStatusUpdateRequest statusUpdateRequestDto,
                                                              @PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return requestService.updateRequestStatus(statusUpdateRequestDto, userId, eventId);
    }
}
