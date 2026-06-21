package com.rahulsmgv.parkinglot.service.pricing;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarFeeStrategyTest {

    @Test
    void shouldCalculateCarFeeForTwoHours() {

        CarFeeStrategy strategy = new CarFeeStrategy();

        LocalDateTime entryTime = LocalDateTime.now().minusHours(2);

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = strategy.calculateFee(
                entryTime,
                exitTime);

        assertEquals(40.0, fee);
    }

    @Test
    void shouldChargeMinimumOneHourForCar() {

        CarFeeStrategy strategy = new CarFeeStrategy();

        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(10);

        LocalDateTime exitTime = LocalDateTime.now();

        double fee = strategy.calculateFee(
                entryTime,
                exitTime);

        assertEquals(20.0, fee);
    }
}