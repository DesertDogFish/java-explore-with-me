package ru.practicum.explorewithme.ewmservice.location.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
@Setter
@Getter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat", nullable = false)
    private Double lat;
    @Column(name = "lon", nullable = false)
    private Double lon;
}