package com.rahulsmgv.parkinglot.kafka.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VehicleParkedEvent {

    /*
     * Ticket generated
     */
    private String ticketId;

    /*
     * Vehicle number
     */
    private String vehicleNumber;

    /*
     * Allocated parking spot
     */
    private String spotNo;

    /*
     * Entry timestamp
     */
    private LocalDateTime entryTime;
}