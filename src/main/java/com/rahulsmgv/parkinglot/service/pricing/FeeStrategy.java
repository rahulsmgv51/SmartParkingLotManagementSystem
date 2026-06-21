package com.rahulsmgv.parkinglot.service.pricing;

import java.time.LocalDateTime;

public interface FeeStrategy {

    /*
     * Calculate parking charges
     */
    double calculateFee(
            LocalDateTime entryTime,
            LocalDateTime exitTime);
}