package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.UserService;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return userService.addUser(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") int size) {
        return userService. getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
