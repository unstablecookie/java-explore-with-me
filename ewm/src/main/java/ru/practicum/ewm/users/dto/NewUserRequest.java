package ru.practicum.ewm.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.util.CustomEmailValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
    @CustomEmailValidator
    private String email;
}
