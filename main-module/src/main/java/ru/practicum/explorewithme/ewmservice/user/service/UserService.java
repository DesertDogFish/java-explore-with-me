package ru.practicum.explorewithme.ewmservice.user.service;


import ru.practicum.explorewithme.ewmservice.user.dto.UserDto;
import ru.practicum.explorewithme.ewmservice.user.dto.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto save(UserShortDto userShortDto);

    List<UserDto> get(List<Long> ids, Integer from, Integer size);

    void delete(Long userId);
}
