package com.rahulsmgv.parkinglot.config;

import org.springframework.stereotype.Component;

import com.rahulsmgv.parkinglot.entity.ParkingSpot;
import com.rahulsmgv.parkinglot.enums.VehicleType;
import com.rahulsmgv.parkinglot.repository.ParkingSpotRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParkingLotInitializer {

    private final ParkingSpotRepository repository;
    private final ParkingProperties properties;    

    @PostConstruct
    public void initialize() {

        // Prevent duplicate initialization
        if (repository.count() > 0) {
            log.info("Parking spots already initialized");
            return;
        }

        int floors = properties.getFloors() == null || properties.getFloors() < 1 ? 1 : properties.getFloors();
        Integer carCount = properties.getSpots().getCar() == null ? 0 : properties.getSpots().getCar();
        Integer bikeCount = properties.getSpots().getBike() == null ? 0 : properties.getSpots().getBike();
        Integer truckCount = properties.getSpots().getTruck() == null ? 0 : properties.getSpots().getTruck();

        // Log configured values for verification
        log.info("Configured floors={}", floors);
        log.info("Configured car spots per floor={}", carCount);
        log.info("Configured bike spots per floor={}", bikeCount);
        log.info("Configured truck spots per floor={}", truckCount);

        for (int floor = 1; floor <= floors; floor++) {
            createSpots(VehicleType.CAR, carCount, "C", floor);
            createSpots(VehicleType.BIKE, bikeCount, "B", floor);
            createSpots(VehicleType.TRUCK, truckCount, "T", floor);
        }

        log.info("Parking lot initialized successfully");
    }

    /**
     * Creates parking spots for a vehicle type
     */
    private void createSpots(
            VehicleType vehicleType,
            Integer count,
            String prefix,
            int floor) {

        if (count == null || count <= 0) {
            log.info("No {} spots to create for floor {}", vehicleType, floor);
            return;
        }

        for (int i = 1; i <= count; i++) {

            ParkingSpot spot = new ParkingSpot();

            spot.setFloorNo(floor);

            spot.setSpotNo("F" + floor + "-" + prefix + "-" + String.format("%02d", i));

            spot.setVehicleType(vehicleType);

            spot.setOccupied(false);

            repository.save(spot);
        }

        log.info("Created {} {} spots on floor {}", count, vehicleType, floor);
    }
}
