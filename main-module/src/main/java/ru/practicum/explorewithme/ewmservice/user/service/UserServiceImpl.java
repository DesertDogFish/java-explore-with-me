package ru.practicum.explorewithme.ewmservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.ewmservice.exception.NotFoundException;
import ru.practicum.explorewithme.ewmservice.user.mapper.UserDtoMapper;
import ru.practicum.explorewithme.ewmservice.user.mapper.UserShortDtoMapper;
import ru.practicum.explorewithme.ewmservice.user.model.User;
import ru.practicum.explorewithme.ewmservice.user.dao.UserDao;
import ru.practicum.explorewithme.ewmservice.user.dto.UserDto;
import ru.practicum.explorewithme.ewmservice.user.dto.UserShortDto;
import ru.practicum.explorewithme.ewmservice.common.PageRequestHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {
    private final UserShortDtoMapper userShortDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final UserDao userDao;

    @Transactional
    @Override
    public UserDto save(UserShortDto dto) {
        return Optional.of(dto)
                .map(userShortDtoMapper::toModel)
                .map(userDao::save)
                .map(userDtoMapper::toDto)
                .get();
    }

    @Override
    public List<UserDto> get(List<Long> ids, Integer from, Integer size) {
        Page<User> result;
        if (ids == null) {
            result = userDao.findAll(new PageRequestHelper(from, size, null));
        } else {
            result = userDao.findAllByIdIn(ids, new PageRequestHelper(from, size, null));
        }
        return result.getContent().stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!userDao.existsById(id)) {
            throw new NotFoundException(String.format("User with id=%d was not found", id));
        }
        userDao.deleteById(id);
    }
}
