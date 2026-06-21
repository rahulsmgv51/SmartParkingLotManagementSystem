package com.rahulsmgv.parkinglot.repository;

import com.rahulsmgv.parkinglot.entity.ParkingSpot;
import com.rahulsmgv.parkinglot.enums.VehicleType;

import java.util.Optional;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

        /*
         * Lock row while allocating spot
         * Prevents same spot being assigned twice
         */
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        Optional<ParkingSpot> findFirstByVehicleTypeAndOccupiedFalseOrderByIdAsc(VehicleType vehicleType);

        /*
         * Total parking spots
         */
        long count();

        /*
         * Available parking spots
         */
        long countByOccupiedFalse();

        /*
         * Occupied parking spots
         */
        long countByOccupiedTrue();

        /*
         * Available spots by vehicle type
         */
        long countByVehicleTypeAndOccupiedFalse(VehicleType vehicleType);
}