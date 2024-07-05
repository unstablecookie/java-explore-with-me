package ru.practicum.ewm.users.dto;

import ru.practicum.ewm.users.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static List<UserDto> toUsersDto(List<User> users) {
        return users.stream().map(x -> toUserDto(x)).collect(Collectors.toList());
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User updateUserWithUser(User oldUser, User newUser) {
        User updatedUser = User.builder()
                .id(oldUser.getId())
                .name((newUser.getName()) != null ? newUser.getName() : oldUser.getName())
                .email((newUser.getEmail()) != null ? newUser.getEmail() : oldUser.getEmail())
                .build();
        return updatedUser;
    }
}
