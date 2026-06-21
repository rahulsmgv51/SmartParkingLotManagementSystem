package com.rahulsmgv.parkinglot.service.pricing;

import com.rahulsmgv.parkinglot.entity.Ticket;
import com.rahulsmgv.parkinglot.enums.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceImplTest {

    private PricingServiceImpl pricingService;

    @BeforeEach
    void setup() {

        pricingService = new PricingServiceImpl(
                new CarFeeStrategy(),
                new BikeFeeStrategy(),
                new TruckFeeStrategy());
    }

    @Test
    void shouldCalculateCarFee() {

        Ticket ticket = Ticket.builder()
                .vehicleType(VehicleType.CAR)
                .entryTime(
                        LocalDateTime.now().minusHours(2))
                .build();

        double fee = pricingService.calculateFee(
                ticket);

        assertEquals(40.0, fee);
    }

    @Test
    void shouldCalculateBikeFee() {

        Ticket ticket = Ticket.builder()
                .vehicleType(VehicleType.BIKE)
                .entryTime(
                        LocalDateTime.now().minusHours(2))
                .build();

        double fee = pricingService.calculateFee(
                ticket);

        assertEquals(20.0, fee);
    }

    @Test
    void shouldCalculateTruckFee() {

        Ticket ticket = Ticket.builder()
                .vehicleType(VehicleType.TRUCK)
                .entryTime(
                        LocalDateTime.now().minusHours(2))
                .build();

        double fee = pricingService.calculateFee(
                ticket);

        assertEquals(100.0, fee);
    }
}