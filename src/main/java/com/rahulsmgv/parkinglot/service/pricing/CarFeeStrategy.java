package com.rahulsmgv.parkinglot.service.pricing;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CarFeeStrategy
                implements FeeStrategy {

        @Override
        public double calculateFee(
                        LocalDateTime entryTime,
                        LocalDateTime exitTime) {

                long hours = Math.max(
                                Duration.between(
                                                entryTime,
                                                exitTime)
                                                .toHours(),
                                1);

                return hours * 20.0;
        }
}