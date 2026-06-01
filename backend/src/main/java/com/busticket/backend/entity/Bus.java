package com.busticket.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusType type;

    @Column(nullable = false)
    private Integer totalSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusStatus status;

    public enum BusType {
        SEAT, SLEEPER, LIMOUSINE
    }

    public enum BusStatus {
        ACTIVE, MAINTENANCE, OUT_OF_SERVICE
    }
}
