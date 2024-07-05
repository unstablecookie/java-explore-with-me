package ru.practicum.ewm.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    @Size(min = 1, max = 50)
    private String name;
}
