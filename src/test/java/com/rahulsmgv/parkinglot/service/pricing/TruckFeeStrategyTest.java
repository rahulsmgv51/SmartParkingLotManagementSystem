package com.rahulsmgv.parkinglot.service.pricing;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TruckFeeStrategyTest {

    @Test
    void shouldCalculateTruckFeeForThreeHours() {

        TruckFeeStrategy strategy = new TruckFeeStrategy();

        LocalDateTime entryTime = LocalDateTime.now().minusHours(3);

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = strategy.calculateFee(
                entryTime,
                exitTime);

        assertEquals(150.0, fee);
    }

    @Test
    void shouldChargeMinimumOneHourForTruck() {

        TruckFeeStrategy strategy = new TruckFeeStrategy();

        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(15);

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = strategy.calculateFee(
                entryTime,
                exitTime);

        assertEquals(50.0, fee);
    }
}