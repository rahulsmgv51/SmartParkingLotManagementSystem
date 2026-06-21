package com.rahulsmgv.parkinglot.kafka.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleExitedEvent {

    private String ticketId;

    private String vehicleNumber;

    private Double parkingFee;

    private LocalDateTime exitTime;
}