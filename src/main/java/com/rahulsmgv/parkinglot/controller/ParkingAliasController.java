package com.rahulsmgv.parkinglot.controller;

import com.rahulsmgv.parkinglot.dto.request.EntryRequest;
import com.rahulsmgv.parkinglot.dto.request.ExitRequest;
import com.rahulsmgv.parkinglot.dto.response.ActiveVehicleResponse;
import com.rahulsmgv.parkinglot.dto.response.AvailableSlotsResponse;
import com.rahulsmgv.parkinglot.dto.response.EntryResponse;
import com.rahulsmgv.parkinglot.dto.response.ExitResponse;
import com.rahulsmgv.parkinglot.dto.response.FeeResponse;
import com.rahulsmgv.parkinglot.service.CacheService;
import com.rahulsmgv.parkinglot.service.DashboardService;
import com.rahulsmgv.parkinglot.service.ParkingService;
import com.rahulsmgv.parkinglot.util.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ParkingAliasController {

    private final ParkingService parkingService;
    private final DashboardService dashboardService;
    private final CacheService cacheService;

    @PostMapping("/entry")
    public ResponseEntity<EntryResponse> parkVehicle(@RequestBody EntryRequest request) {
        log.info("Step 1 - received alias entry request for vehicleNumber={}", request.getVehicleNumber());
        return ResponseEntity.ok(parkingService.parkVehicle(request));
    }

    @PostMapping("/exit")
    public ResponseEntity<ExitResponse> exitVehicle(@RequestBody ExitRequest request) {
        log.info("Step 1 - received alias exit request for ticketId={} vehicleNumber={}", request.getTicketId(), request.getVehicleNumber());
        return ResponseEntity.ok(parkingService.exitVehicle(request));
    }

    @GetMapping("/vehicles/active")
    public ResponseEntity<List<ActiveVehicleResponse>> getActiveVehicles() {
        return ResponseEntity.ok(dashboardService.getActiveVehicles());
    }

    @GetMapping("/slots/available")
    public ResponseEntity<AvailableSlotsResponse> getAvailableSlots() {
        Long availableCarSpots = safeCounter(cacheService.getCounter(RedisKeys.AVAILABLE_CAR_SPOTS));
        Long availableBikeSpots = safeCounter(cacheService.getCounter(RedisKeys.AVAILABLE_BIKE_SPOTS));
        Long availableTruckSpots = safeCounter(cacheService.getCounter(RedisKeys.AVAILABLE_TRUCK_SPOTS));
        Long totalAvailableSpots = availableCarSpots + availableBikeSpots + availableTruckSpots;

        AvailableSlotsResponse response = AvailableSlotsResponse.builder()
                .availableCarSpots(availableCarSpots)
                .availableBikeSpots(availableBikeSpots)
                .availableTruckSpots(availableTruckSpots)
                .totalAvailableSpots(totalAvailableSpots)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/fee/calculate")
    public ResponseEntity<FeeResponse> calculateFee(@RequestBody ExitRequest request) {
        double parkingFee = parkingService.calculateFee(request);

        FeeResponse response = FeeResponse.builder()
                .ticketId(request.getTicketId())
                .vehicleNumber(request.getVehicleNumber())
                .parkingFee(parkingFee)
                .build();

        return ResponseEntity.ok(response);
    }

    private Long safeCounter(Long value) {
        return value == null ? 0L : value;
    }
}