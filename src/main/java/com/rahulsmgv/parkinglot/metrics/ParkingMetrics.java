package com.rahulsmgv.parkinglot.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Component;

@Component
public class ParkingMetrics {

    private final Counter vehicleEntryCounter;

    private final Counter vehicleExitCounter;

    public ParkingMetrics(
            MeterRegistry registry) {

        this.vehicleEntryCounter =
                Counter.builder(
                        "parking.vehicle.entry")
                        .description(
                                "Vehicle Entries")
                        .register(registry);

        this.vehicleExitCounter =
                Counter.builder(
                        "parking.vehicle.exit")
                        .description(
                                "Vehicle Exits")
                        .register(registry);
    }

    public void incrementEntry() {

        vehicleEntryCounter.increment();
    }

    public void incrementExit() {

        vehicleExitCounter.increment();
    }
}