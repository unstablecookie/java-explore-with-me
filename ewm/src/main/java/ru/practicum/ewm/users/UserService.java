package ru.practicum.ewm.users;

import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);
}
