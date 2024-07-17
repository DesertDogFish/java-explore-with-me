package ru.practicum.explorewithme.ewmservice.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.ewmservice.user.dto.UserShortDto;
import ru.practicum.explorewithme.ewmservice.user.model.User;

@Mapper(componentModel = "spring")
public interface UserShortDtoMapper {

    User toModel(UserShortDto userShortDto);
}
