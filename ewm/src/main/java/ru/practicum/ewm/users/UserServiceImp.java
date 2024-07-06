package ru.practicum.ewm.users;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.error.EntityNotFoundException;
import ru.practicum.ewm.error.IntegrityConflictException;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.EventMapper;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.users.dto.NewUserRequest;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserMapper;
import ru.practicum.ewm.users.model.User;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        try {
            User createdUser = userRepository.save(UserMapper.toUser(newUserRequest));
            return UserMapper.toUserDto(createdUser);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityConflictException(e.getMessage(), "Integrity constraint has been violated.");
        }
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (ids == null) {
            return userRepository.findAll(page).stream()
                    .map(x -> UserMapper.toUserDto(x))
                    .collect(Collectors.toList());
        }
        return userRepository.findByIdIn(ids, page).stream()
                .map(x -> UserMapper.toUserDto(x))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        "The required object was not found.")
        );
        userRepository.delete(user);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id=%d was not found", userId),
                        "The required object was not found.")
        );
        return eventRepository.findByInitiatorId(userId, page).stream()
                .map(x -> EventMapper.toEventShortDto(x))
                .collect(Collectors.toList());
    }
}
