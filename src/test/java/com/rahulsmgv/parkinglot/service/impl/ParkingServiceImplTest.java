package com.rahulsmgv.parkinglot.service.impl;

import com.rahulsmgv.parkinglot.dto.request.EntryRequest;
import com.rahulsmgv.parkinglot.dto.request.ExitRequest;
import com.rahulsmgv.parkinglot.dto.response.EntryResponse;
import com.rahulsmgv.parkinglot.dto.response.ExitResponse;
import com.rahulsmgv.parkinglot.entity.ParkingSpot;
import com.rahulsmgv.parkinglot.entity.Ticket;
import com.rahulsmgv.parkinglot.enums.TicketStatus;
import com.rahulsmgv.parkinglot.enums.VehicleType;
import com.rahulsmgv.parkinglot.exception.ParkingException;
import com.rahulsmgv.parkinglot.kafka.producer.ParkingEventProducer;
import com.rahulsmgv.parkinglot.repository.ParkingSpotRepository;
import com.rahulsmgv.parkinglot.repository.TicketRepository;
import com.rahulsmgv.parkinglot.repository.VehicleRepository;
import com.rahulsmgv.parkinglot.entity.Vehicle;
import com.rahulsmgv.parkinglot.service.CacheService;
import com.rahulsmgv.parkinglot.service.pricing.PricingService;
import com.rahulsmgv.parkinglot.metrics.ParkingMetrics;
import com.rahulsmgv.parkinglot.util.RedisKeys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceImplTest {

    @Mock
    private ParkingSpotRepository spotRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private PricingService pricingService;

        @Mock
        private ParkingMetrics parkingMetrics;

    @Mock
    private CacheService cacheService;

        @Mock
        private VehicleRepository vehicleRepository;

    @Mock
    private ParkingEventProducer producer;

    @InjectMocks
    private ParkingServiceImpl parkingService;

    private ParkingSpot spot;

    @BeforeEach
    void setup() {

        spot = ParkingSpot.builder()
                .id(1L)
                .spotNo("C-01")
                .vehicleType(VehicleType.CAR)
                .occupied(false)
                .build();
    }

    /**
     * Vehicle entry success flow
     */
    @Test
    void shouldParkVehicleSuccessfully() {

        EntryRequest request = new EntryRequest();
        request.setVehicleNumber("UP32AB1234");
        request.setVehicleType(VehicleType.CAR);

        when(spotRepository
                .findFirstByVehicleTypeAndOccupiedFalseOrderByIdAsc(
                        VehicleType.CAR))
                .thenReturn(Optional.of(spot));

        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(vehicleRepository.findByVehicleNumber("UP32AB1234"))
                .thenReturn(Optional.empty());

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(i -> i.getArgument(0));

        EntryResponse response =
                parkingService.parkVehicle(request);

        assertNotNull(response);
        assertEquals("C-01", response.getSpotNo());

        verify(spotRepository).save(any());
        verify(ticketRepository).save(any());
        verify(producer).publishVehicleParkedEvent(any());

        verify(cacheService, times(2)).increment(any());
        verify(cacheService).decrement(RedisKeys.AVAILABLE_CAR_SPOTS);
    }

    /**
     * No spot available
     */
    @Test
    void shouldThrowExceptionWhenNoSpotAvailable() {

        EntryRequest request = new EntryRequest();
        request.setVehicleType(VehicleType.CAR);

        when(spotRepository
                .findFirstByVehicleTypeAndOccupiedFalseOrderByIdAsc(
                        VehicleType.CAR))
                .thenReturn(Optional.empty());

        assertThrows(
                ParkingException.class,
                () -> parkingService.parkVehicle(request));
    }

    /**
     * Vehicle exit success
     */
    @Test
    void shouldExitVehicleSuccessfully() {

        Ticket ticket = Ticket.builder()
                .ticketId("T1")
                .vehicleNumber("UP32AB1234")
                .vehicleType(VehicleType.CAR)
                .spotId(1L)
                .status(TicketStatus.ACTIVE)
                .entryTime(LocalDateTime.now().minusHours(2))
                .build();

        ExitRequest request = new ExitRequest();
        request.setTicketId("T1");

        when(ticketRepository
                .findByTicketIdAndStatus(
                        "T1",
                        TicketStatus.ACTIVE))
                .thenReturn(Optional.of(ticket));

        when(spotRepository.findById(1L))
                .thenReturn(Optional.of(spot));

        when(pricingService.calculateFee(ticket))
                .thenReturn(40.0);

        ExitResponse response =
                parkingService.exitVehicle(request);

        assertNotNull(response);

        assertEquals(
                "UP32AB1234",
                response.getVehicleNumber());

        assertEquals(
                40.0,
                response.getParkingFee());

        verify(ticketRepository).save(any());
        verify(producer).publishVehicleExitedEvent(any());
    }

    /**
     * Ticket not found
     */
    @Test
    void shouldThrowExceptionWhenTicketNotFound() {

        ExitRequest request = new ExitRequest();
        request.setTicketId("INVALID");

        when(ticketRepository
                .findByTicketIdAndStatus(
                        "INVALID",
                        TicketStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(
                ParkingException.class,
                () -> parkingService.exitVehicle(request));
    }
}