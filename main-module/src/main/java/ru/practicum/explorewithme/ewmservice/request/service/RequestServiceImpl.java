package ru.practicum.explorewithme.ewmservice.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewmservice.event.model.Event;
import ru.practicum.explorewithme.ewmservice.event.model.enums.EventState;
import ru.practicum.explorewithme.ewmservice.event.dao.EventDao;
import ru.practicum.explorewithme.ewmservice.exception.ConflictException;
import ru.practicum.explorewithme.ewmservice.exception.NotFoundException;
import ru.practicum.explorewithme.ewmservice.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.ewmservice.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.ewmservice.request.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.ewmservice.request.mapper.ParticipationRequestDtoMapper;
import ru.practicum.explorewithme.ewmservice.request.model.Request;
import ru.practicum.explorewithme.ewmservice.request.model.enums.RequestStatus;
import ru.practicum.explorewithme.ewmservice.request.dao.RequestDao;
import ru.practicum.explorewithme.ewmservice.user.model.User;
import ru.practicum.explorewithme.ewmservice.user.dao.UserDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestDao requestDao;
    private final UserDao userDao;
    private final EventDao eventDao;
    private final ParticipationRequestDtoMapper participationRequestDtoMapper;

    @Override
    @Transactional
    public ParticipationRequestDto save(Long userId, Long eventId) {
        User user = getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);

        checkIfUserIsEventInitiatorAndThrowException(userId, event.getInitiator().getId());
        checkIfEventIsPublishedAndThrowException(eventId, event.getState());
        checkIfParticipantLimitFullAndThrowException(event);

        return Optional.of(createRequest(user, event))
                .map(requestDao::save)
                .map(participationRequestDtoMapper::toDto)
                .get();
    }

    @Override
    public List<ParticipationRequestDto> getAllForRequester(Long userId) {
        getUserOrThrowNotFoundException(userId);
        return requestDao.findAllByRequesterId(userId).stream()
                .map(participationRequestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestsId) {
        getUserOrThrowNotFoundException(userId);
        Request request = getRequestOrThrowNotFoundException(requestsId);

        checkIfRequesterIsOwnerAndThrowException(userId, request.getRequester().getId(), requestsId);

        request.setStatus(RequestStatus.CANCELED);
        return Optional.of(request)
                .map(requestDao::save)
                .map(participationRequestDtoMapper::toDto)
                .get();
    }

    @Override
    public List<ParticipationRequestDto> get(Long userId, Long eventId) {
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        checkIfUserIsEventOwnerAndThrowException(event, userId);

        return requestDao.findAllByEventId(eventId).stream()
                .map(participationRequestDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(
            EventRequestStatusUpdateRequest dto, Long userId, Long eventId) {

        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        checkIfUserIsEventOwnerAndThrowException(event, userId);

        if (dto.getStatus().equals(RequestStatus.CONFIRMED)) {
            if (event.getParticipantLimit() != 0) {
                Integer countRequestsLimit = requestDao.countAllByEventIdAndStatus(event.getId(),
                        RequestStatus.CONFIRMED) + dto.getRequestIds().size();

                if (countRequestsLimit > event.getParticipantLimit()) {
                    throw new ConflictException("The participant limit has been reached");
                }
                List<Request> requests = updateRequestStatus(dto, eventId);

                EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                        .confirmedRequests(requests.stream()
                                .map(participationRequestDtoMapper::toDto)
                                .collect(Collectors.toList())).build();

                if (countRequestsLimit.equals(event.getParticipantLimit())) {
                    List<Request> otherRequests = requestDao.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                    otherRequests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                    requestDao.saveAll(otherRequests);

                    result.setRejectedRequests(otherRequests.stream()
                            .map(participationRequestDtoMapper::toDto)
                            .collect(Collectors.toList()));

                }
                return result;
            } else {
                List<Request> requests = updateRequestStatus(dto, eventId);
                return EventRequestStatusUpdateResult.builder()
                        .confirmedRequests(requests.stream()
                                .map(participationRequestDtoMapper::toDto)
                                .collect(Collectors.toList())).build();
            }
        } else {
            List<Request> rejectedRequest = updateRequestStatus(dto, eventId);

            return EventRequestStatusUpdateResult.builder()
                    .rejectedRequests(rejectedRequest.stream()
                            .map(participationRequestDtoMapper::toDto)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Override
    public Map<Long, Integer> countConfirmedRequests(List<Long> eventIds) {
        List<Request> eventsByIdInAndStatus = requestDao.findAllByEventIdInAndStatus(eventIds,
                RequestStatus.CONFIRMED);

        return eventsByIdInAndStatus.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getEvent().getId(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue))
                );
    }

    private List<Request> updateRequestStatus(EventRequestStatusUpdateRequest dto, Long eventId) {
        List<Request> requests = requestDao.findAllByIdInAndEventId(dto.getRequestIds(),
                eventId);
        requests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Request must have status PENDING");
            }
            request.setStatus(dto.getStatus());
        });
        requestDao.saveAll(requests);
        return requests;
    }

    private void checkIfUserIsEventOwnerAndThrowException(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of event id=%d", userId, event.getId())
            );
        }
    }

    private void checkIfRequesterIsOwnerAndThrowException(Long userId, Long requesterId, Long requestsId) {
        if (!requesterId.equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of request id=%d", userId, requestsId)
            );
        }
    }

    private Request getRequestOrThrowNotFoundException(Long requestsId) {
        return requestDao.findById(requestsId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Request with id=%d was not found", requestsId))
                );
    }

    private void checkIfParticipantLimitFullAndThrowException(Event event) {
        if (event.getParticipantLimit() != 0 &&
                requestDao.countAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                        .equals(event.getParticipantLimit())) {
            throw new ConflictException(
                    String.format("The limit of participants has been reached for the event with id=%d", event.getId())
            );
        }
    }

    private static void checkIfEventIsPublishedAndThrowException(Long eventId, EventState state) {
        if (!state.equals(EventState.PUBLISHED)) {
            throw new ConflictException(
                    String.format("Event with id=%d is not published", eventId)
            );
        }
    }

    private void checkIfUserIsEventInitiatorAndThrowException(Long userId, Long eventId) {
        if (eventId.equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d is initiator event id=%d", userId, eventId)
            );
        }
    }

    private Request createRequest(User user, Event event) {
        return Request.builder()
                .created(LocalDateTime.now().withNano(0))
                .event(event)
                .requester(user)
                .status((event.getRequestModeration() && event.getParticipantLimit() != 0) ?
                        RequestStatus.PENDING : RequestStatus.CONFIRMED).build();
    }

    private Event getEventOrThrowNotFoundException(Long eventId) {
        return eventDao.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private User getUserOrThrowNotFoundException(Long userId) {
        return userDao.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", userId))
        );
    }
}
