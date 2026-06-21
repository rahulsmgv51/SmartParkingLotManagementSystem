package com.rahulsmgv.parkinglot.service.impl;

import com.rahulsmgv.parkinglot.dto.response.ActiveVehicleResponse;
import com.rahulsmgv.parkinglot.dto.response.DashboardResponse;
import com.rahulsmgv.parkinglot.enums.TicketStatus;
import com.rahulsmgv.parkinglot.repository.TicketRepository;
import com.rahulsmgv.parkinglot.service.CacheService;
import com.rahulsmgv.parkinglot.service.DashboardService;
import com.rahulsmgv.parkinglot.util.RedisKeys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TicketRepository ticketRepository;
    private final CacheService cacheService;

    /**
     * Build the dashboard summary using cached counters.
     *
     * This avoids expensive database aggregation by relying on Redis counters
     * that are kept up to date during entry and exit events.
     */
    @Override
    public DashboardResponse getSummary() {
        log.info("Step 1 - Starting dashboard summary calculation");
        log.info("Step 2 - Fetching cache counters from Redis");
        
        Long totalSpots = cacheService.getCounter(RedisKeys.TOTAL_SPOTS);
        Long occupiedSpots = cacheService.getCounter(RedisKeys.OCCUPIED_SPOTS);
        Long activeVehicles = cacheService.getCounter(RedisKeys.ACTIVE_VEHICLES);
        Long availableSpots = totalSpots - occupiedSpots;

        log.info("Step 3 - Dashboard summary produced: totalSpots={}, occupiedSpots={}, activeVehicles={}, availableSpots={}",
                totalSpots,
                occupiedSpots,
                activeVehicles,
                availableSpots);

        return DashboardResponse.builder()
                .totalSpots(totalSpots)
                .availableSpots(availableSpots)
                .occupiedSpots(occupiedSpots)
                .activeVehicles(activeVehicles)
                .build();
    }

    /**
     * Retrieve all currently active parking tickets from the ticket store.
     */
    @Override
    public List<ActiveVehicleResponse> getActiveVehicles() {
        log.info("Step 1 - Starting retrieval of active vehicles");
        log.info("Step 2 - Querying ticket repository for ACTIVE tickets");
        
        List<ActiveVehicleResponse> activeVehicles = ticketRepository.findByStatus(TicketStatus.ACTIVE)
                .stream()
                .map(ticket -> ActiveVehicleResponse.builder()
                        .ticketId(ticket.getTicketId())
                        .vehicleNumber(ticket.getVehicleNumber())
                        .spotId(ticket.getSpotId())
                        .entryTime(ticket.getEntryTime())
                        .build())
                .toList();

        log.info("Step 3 - Retrieved {} active vehicles from ticket store", activeVehicles.size());
        log.debug("Step 3 detail - Active vehicles detail list: {}", activeVehicles);

        return activeVehicles;
    }
}