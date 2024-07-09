package ru.practicum.ewm.locations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.locations.types.model.LocationType;
import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "lat")
    private Float lat;
    @Column(name = "lon", unique = true)
    private Float lon;
    @Column(name = "radius")
    private Float radius;
    @Column(name = "name", unique = true)
    private String name;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_type_id")
    private LocationType type;
}
