package com.rahulsmgv.parkinglot.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingEventProducerImpl
        implements ParkingEventProducer {

    private static final String TOPIC =
            "parking-events";

    private final KafkaTemplate<String, Object>
            kafkaTemplate;

    @Override
    public void publishVehicleParkedEvent(
            Object event) {

        kafkaTemplate.send(
                TOPIC,
                event);
    }

    @Override
    public void publishVehicleExitedEvent(
            Object event) {

        kafkaTemplate.send(
                TOPIC,
                event);
    }
}