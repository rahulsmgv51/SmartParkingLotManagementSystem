package com.rahulsmgv.parkinglot.entity;

import com.rahulsmgv.parkinglot.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "floor_no", nullable = false)
    private Integer floorNo;

    @Column(name = "spot_no", nullable = false, unique = true)
    private String spotNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private Boolean occupied;
}