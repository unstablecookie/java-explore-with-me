package ru.practicum.ewm.locations.types.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewLocationTypeDto {
    @Size(min = 3, max = 50)
    private String name;
}
