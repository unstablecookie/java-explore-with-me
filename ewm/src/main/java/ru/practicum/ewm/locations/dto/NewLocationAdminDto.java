package ru.practicum.ewm.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewLocationAdminDto {
    @Min(value = -90)
    @Max(value = 90)
    private Float lat;
    @Min(value = -180)
    @Max(value = 180)
    private Float lon;
    @NotNull
    private Float radius;
    private String name;
    private Long type;
}
