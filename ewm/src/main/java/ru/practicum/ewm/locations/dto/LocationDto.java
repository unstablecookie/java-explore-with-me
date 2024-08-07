package ru.practicum.ewm.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    private Float lat;
    private Float lon;
    private Float radius;
    private String name;
    private Long type;
}
