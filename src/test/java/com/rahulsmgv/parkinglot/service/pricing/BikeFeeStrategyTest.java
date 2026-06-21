package com.rahulsmgv.parkinglot.service.pricing;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BikeFeeStrategyTest {

    @Test
    void shouldCalculateBikeFeeForOneHour() {

        BikeFeeStrategy strategy = new BikeFeeStrategy();

        LocalDateTime entryTime = LocalDateTime.now().minusHours(1);

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = strategy.calculateFee(
                entryTime,
                exitTime);

        assertEquals(10.0, fee);
    }

    @Test
    void shouldChargeMinimumOneHourForBike() {

        BikeFeeStrategy strategy = new BikeFeeStrategy();

        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(5);

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = strategy.calculateFee(
                entryTime,
                exitTime);

        assertEquals(10.0, fee);
    }
}