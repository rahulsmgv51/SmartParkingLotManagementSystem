package com.rahulsmgv.parkinglot.service.impl;

import com.rahulsmgv.parkinglot.dto.request.EntryRequest;
import com.rahulsmgv.parkinglot.dto.request.ExitRequest;
import com.rahulsmgv.parkinglot.dto.response.EntryResponse;
import com.rahulsmgv.parkinglot.dto.response.ExitResponse;
import com.rahulsmgv.parkinglot.entity.ParkingSpot;
import com.rahulsmgv.parkinglot.entity.Ticket;
import com.rahulsmgv.parkinglot.enums.TicketStatus;
import com.rahulsmgv.parkinglot.exception.ParkingException;
import com.rahulsmgv.parkinglot.kafka.event.VehicleExitedEvent;
import com.rahulsmgv.parkinglot.kafka.event.VehicleParkedEvent;
import com.rahulsmgv.parkinglot.kafka.producer.ParkingEventProducer;
import com.rahulsmgv.parkinglot.metrics.ParkingMetrics;
import com.rahulsmgv.parkinglot.repository.ParkingSpotRepository;
import com.rahulsmgv.parkinglot.repository.TicketRepository;
import com.rahulsmgv.parkinglot.service.CacheService;
import com.rahulsmgv.parkinglot.service.ParkingService;
import com.rahulsmgv.parkinglot.service.pricing.PricingService;
import com.rahulsmgv.parkinglot.util.RedisKeys;
import com.rahulsmgv.parkinglot.util.TracingConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Parking service implementation responsible for entry and exit flows.
 *
 * This service handles:
 *  - finding and reserving available parking spots
 *  - creating and closing parking tickets
 *  - calculating parking fees
 *  - publishing Kafka events
 *  - updating Redis counters for dashboard metrics
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {

        private final ParkingSpotRepository spotRepository;
        private final TicketRepository ticketRepository;
        private final PricingService pricingService;
        private final CacheService cacheService;
        private final ParkingEventProducer producer;
        private final ParkingMetrics parkingMetrics;
        private final com.rahulsmgv.parkinglot.repository.VehicleRepository vehicleRepository;

        /**
         * Allocate a parking space and issue a ticket for vehicle entry.
         *
         * The method enforces a single active ticket per vehicle, reserves an
         * available spot, persists the ticket state, publishes a parking event,
         * and updates Redis counters for dashboard metrics.
         */
        @Override
        @Transactional
        public EntryResponse parkVehicle(EntryRequest request) {

                log.info("Starting parking entry for vehicleNumber={} vehicleType={} correlationId={} sessionId={} userId={}",
                        request.getVehicleNumber(),
                        request.getVehicleType(),
                        MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY),
                        MDC.get(TracingConstants.MDC_SESSION_ID_KEY),
                        MDC.get(TracingConstants.MDC_USER_ID_KEY));

                log.info("Step 2 - validating that vehicle is not already parked, vehicleNumber={}", request.getVehicleNumber());

                ticketRepository
                                .findByVehicleNumberAndStatus(request.getVehicleNumber(), TicketStatus.ACTIVE)
                                .ifPresent(ticket -> {
                                        throw new ParkingException("Vehicle already parked. Active ticketId=" + ticket.getTicketId());
                                });

                log.info("Step 3 - searching for first available parking spot for vehicleType={} vehicleNumber={}", request.getVehicleType(), request.getVehicleNumber());
                ParkingSpot spot = spotRepository
                                .findFirstByVehicleTypeAndOccupiedFalseOrderByIdAsc(request.getVehicleType())
                                .orElseThrow(() -> new ParkingException("No parking spot available"));

                log.info("Step 4 - allocated parking spot {} for vehicleNumber={}", spot.getSpotNo(), request.getVehicleNumber());
                log.debug("Step 4 detail - allocated parking spot {} for vehicleNumber={} correlationId={} sessionId={} userId={}",
                        spot.getSpotNo(),
                        request.getVehicleNumber(),
                        MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY),
                        MDC.get(TracingConstants.MDC_SESSION_ID_KEY),
                        MDC.get(TracingConstants.MDC_USER_ID_KEY));

                log.info("Step 5 - Marking spot as occupied and persisting changes spotId={}", spot.getId());
                spot.setOccupied(true);
                spotRepository.save(spot);

                log.info("Step 6 - Ensuring vehicle record exists or creating new record for vehicleNumber={}", request.getVehicleNumber());
                com.rahulsmgv.parkinglot.entity.Vehicle vehicle = vehicleRepository
                                .findByVehicleNumber(request.getVehicleNumber())
                                .orElseGet(() -> com.rahulsmgv.parkinglot.entity.Vehicle.builder()
                                                .vehicleNumber(request.getVehicleNumber())
                                                .vehicleType(request.getVehicleType())
                                                .firstSeen(LocalDateTime.now())
                                                .lastSeen(LocalDateTime.now())
                                                .build());

                vehicle.setLastSeen(LocalDateTime.now());
                vehicle.setVehicleType(request.getVehicleType());
                vehicleRepository.save(vehicle);

                log.info("Step 7 - Building and persisting active ticket for vehicleNumber={}", request.getVehicleNumber());
                Ticket ticket = Ticket.builder()
                                .ticketId(UUID.randomUUID().toString())
                                .vehicleNumber(request.getVehicleNumber())
                                .vehicleType(request.getVehicleType())
                                .spotId(spot.getId())
                                .entryTime(LocalDateTime.now())
                                .status(TicketStatus.ACTIVE)
                                .build();

                ticketRepository.save(ticket);

                log.info("Step 8 - Creating and publishing VehicleParkedEvent for ticketId={}", ticket.getTicketId());
                VehicleParkedEvent event = VehicleParkedEvent.builder()
                                .ticketId(ticket.getTicketId())
                                .vehicleNumber(ticket.getVehicleNumber())
                                .spotNo(spot.getSpotNo())
                                .entryTime(ticket.getEntryTime())
                                .build();

                producer.publishVehicleParkedEvent(event);

                log.info("Step 9 - Vehicle parked successfully: ticketId={}, spotNo={}, vehicleNumber={}, correlationId={}, sessionId={}, userId={}",
                        ticket.getTicketId(),
                        spot.getSpotNo(),
                        ticket.getVehicleNumber(),
                        MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY),
                        MDC.get(TracingConstants.MDC_SESSION_ID_KEY),
                        MDC.get(TracingConstants.MDC_USER_ID_KEY));

                log.info("Step 10 - Updating Redis counters for dashboard metrics");
                cacheService.increment(RedisKeys.OCCUPIED_SPOTS);
                cacheService.increment(RedisKeys.ACTIVE_VEHICLES);

                log.info("Step 11 - Decrementing available {} spots counter", request.getVehicleType());
                switch (request.getVehicleType()) {

                        case CAR -> cacheService.decrement( RedisKeys.AVAILABLE_CAR_SPOTS);

                        case BIKE -> cacheService.decrement( RedisKeys.AVAILABLE_BIKE_SPOTS);

                        case TRUCK -> cacheService.decrement( RedisKeys.AVAILABLE_TRUCK_SPOTS);
                }

                log.info("Step 12 - Incrementing parking metrics");
                parkingMetrics.incrementEntry();

                return EntryResponse.builder()
                                .ticketId(ticket.getTicketId())
                                .spotNo(spot.getSpotNo())
                                .entryTime(ticket.getEntryTime())
                                .build();
        }

        /**
         * Close an active parking ticket and free the reserved parking spot.
         *
         * This method retrieves the active ticket, calculates the parking fee,
         * frees the spot, publishes an exit event, and updates dashboard counters.
         */
        @Override
        @Transactional
        public ExitResponse exitVehicle(
                        ExitRequest request) {

                log.info("Step 2 - Starting parking exit for ticketId={} vehicleNumber={} correlationId={} sessionId={} userId={}",
                                request.getTicketId(),
                                request.getVehicleNumber(),
                                MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY),
                                MDC.get(TracingConstants.MDC_SESSION_ID_KEY),
                                MDC.get(TracingConstants.MDC_USER_ID_KEY));

                log.info("Step 2a - Resolving active ticket from request");
                Ticket ticket = resolveActiveTicket(request);

                log.info("Step 2b - Computing parking fee for the session");
                double fee = pricingService.calculateFee(ticket);

                log.debug("Step 2b detail - Calculated parking fee for ticketId={} vehicleNumber={} fee={} correlationId={} sessionId={} userId={}",
                                ticket.getTicketId(),
                                ticket.getVehicleNumber(),
                                fee,
                                MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY),
                                MDC.get(TracingConstants.MDC_SESSION_ID_KEY),
                                MDC.get(TracingConstants.MDC_USER_ID_KEY));

                log.info("Step 2c - Retrieving parking spot from database spotId={}", ticket.getSpotId());
                ParkingSpot spot = spotRepository
                                .findById(ticket.getSpotId())
                                .orElseThrow(() -> new ParkingException("Spot not found"));

                log.info("Step 2d - Marking spot as unoccupied spotNo={}", spot.getSpotNo());
                spot.setOccupied(false);
                spotRepository.save(spot);

                LocalDateTime exitTime = LocalDateTime.now();

                log.info("Step 2e - Updating ticket with exit details and marking as COMPLETED");
                ticket.setExitTime(exitTime);
                ticket.setAmount(fee);
                ticket.setStatus(TicketStatus.COMPLETED);

                ticketRepository.save(ticket);

                log.info("Step 3 - updating ticket and publishing exit event for ticketId={} vehicleNumber={}", ticket.getTicketId(), ticket.getVehicleNumber());
                VehicleExitedEvent event = VehicleExitedEvent.builder()
                                .ticketId(ticket.getTicketId())
                                .vehicleNumber(
                                                ticket.getVehicleNumber())
                                .parkingFee(fee)
                                .exitTime(exitTime)
                                .build();

                producer.publishVehicleExitedEvent(
                                event);

                log.info("Step 4 - vehicle exit completed: ticketId={}, vehicleNumber={}, parkingFee={}",
                                ticket.getTicketId(),
                                ticket.getVehicleNumber(),
                                fee);
                log.debug("Step 4 detail - exit completed correlationId={} sessionId={} userId={}",
                                MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY),
                                MDC.get(TracingConstants.MDC_SESSION_ID_KEY),
                                MDC.get(TracingConstants.MDC_USER_ID_KEY));

                // Redis counter updates for the dashboard.
                cacheService.decrement(RedisKeys.OCCUPIED_SPOTS);

                // Active vehicles decrease
                cacheService.decrement(
                                RedisKeys.ACTIVE_VEHICLES);

                // Available spots increase
                switch (ticket.getVehicleType()) {

                        case CAR ->
                                cacheService.increment(
                                                RedisKeys.AVAILABLE_CAR_SPOTS);

                        case BIKE ->
                                cacheService.increment(
                                                RedisKeys.AVAILABLE_BIKE_SPOTS);

                        case TRUCK ->
                                cacheService.increment(
                                                RedisKeys.AVAILABLE_TRUCK_SPOTS);
                }

                log.info("Step 5 - Incrementing parking exit metrics");
                log.debug("Step 5 detail - Updated cache counters for exit ticketId={}, vehicleType={}",
                                ticket.getTicketId(),
                                ticket.getVehicleType());

                parkingMetrics.incrementExit();

                return ExitResponse.builder()
                                .ticketId(ticket.getTicketId())
                                .vehicleNumber(
                                                ticket.getVehicleNumber())
                                .parkingFee(fee)
                                .entryTime(ticket.getEntryTime())
                                .exitTime(exitTime)
                                .build();
        }

        @Override
        public double calculateFee(ExitRequest request) {
                Ticket ticket = resolveActiveTicket(request);
                return pricingService.calculateFee(ticket);
        }

        private Ticket resolveActiveTicket(ExitRequest request) {
                if (request.getTicketId() != null && !request.getTicketId().isBlank()) {
                        return ticketRepository.findByTicketIdAndStatus(request.getTicketId(), TicketStatus.ACTIVE)
                                        .orElseThrow(() -> new ParkingException("Ticket not found"));
                }

                if (request.getVehicleNumber() != null && !request.getVehicleNumber().isBlank()) {
                        return ticketRepository.findByVehicleNumberAndStatus(request.getVehicleNumber(), TicketStatus.ACTIVE)
                                        .orElseThrow(() -> new ParkingException("Active ticket not found for vehicleNumber=" + request.getVehicleNumber()));
                }

                throw new ParkingException("ticketId or vehicleNumber is required for exit or fee calculation");
        }
}