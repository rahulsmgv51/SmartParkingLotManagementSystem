package com.rahulsmgv.parkinglot.kafka.producer;

public interface ParkingEventProducer {

    void publishVehicleParkedEvent(
            Object event);

    void publishVehicleExitedEvent(
            Object event);
}