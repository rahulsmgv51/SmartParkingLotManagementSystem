package com.rahulsmgv.parkinglot.controller;

import com.rahulsmgv.parkinglot.dto.response.ActiveVehicleResponse;
import com.rahulsmgv.parkinglot.dto.response.DashboardResponse;
import com.rahulsmgv.parkinglot.service.DashboardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /*
     * Dashboard Summary API
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> getSummary() {
        log.info("Step 1 - Received dashboard summary request");
        log.info("Step 2 - Invoking dashboardService.getSummary()");

        DashboardResponse response = dashboardService.getSummary();

        log.info("Step 3 - Dashboard summary retrieved: totalSpots={}, availableSpots={}, occupiedSpots={}, activeVehicles={}",
                response.getTotalSpots(),
                response.getAvailableSpots(),
                response.getOccupiedSpots(),
                response.getActiveVehicles());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active-vehicles")
    public ResponseEntity<List<ActiveVehicleResponse>> getActiveVehicles() {
        log.info("Step 1 - Received active vehicles request");
        log.info("Step 2 - Invoking dashboardService.getActiveVehicles()");

        List<ActiveVehicleResponse> activeVehicles = dashboardService.getActiveVehicles();

        log.info("Step 3 - Active vehicles retrieved: count={}", activeVehicles.size());

        return ResponseEntity.ok(activeVehicles);
    }
}