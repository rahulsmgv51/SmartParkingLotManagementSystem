package com.rahulsmgv.parkinglot.kafka.consumer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ParkingEventConsumer {

    @KafkaListener(
            topics = "parking-events",
            groupId = "parking-group")
    public void consume(
            Object event) {

        /*
         * Future:
         * Send Email
         * Send SMS
         * Audit Logging
         * Analytics
         */

        log.info("Received event from topic parking-events: class={}, payload={}",
                event != null ? event.getClass().getSimpleName() : "null",
                event);
        log.debug("Full consumed event payload: {}", event);
    }
}