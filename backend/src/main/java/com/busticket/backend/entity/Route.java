package com.busticket.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departure_location_id", nullable = false)
    private Location departureLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrival_location_id", nullable = false)
    private Location arrivalLocation;

    @Column(nullable = false)
    private Double distance; // in kilometers

    @Column(nullable = false)
    private String duration; // e.g., "2h 30m"
}
