package ru.practicum.explorewithme.ewmservice.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.ewmservice.user.dto.UserDto;
import ru.practicum.explorewithme.ewmservice.user.dto.UserShortDto;
import ru.practicum.explorewithme.ewmservice.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserAdminController {
    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@Valid @RequestBody UserShortDto userShortDto) {
        return userService.save(userShortDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> get(@RequestParam(required = false) List<Long> ids,
                             @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                             @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return userService.get(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
