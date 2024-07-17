package ru.practicum.explorewithme.ewmservice.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.user.dto.UserDto;
import ru.practicum.explorewithme.ewmservice.user.model.User;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserDto toDto(User user);
}
