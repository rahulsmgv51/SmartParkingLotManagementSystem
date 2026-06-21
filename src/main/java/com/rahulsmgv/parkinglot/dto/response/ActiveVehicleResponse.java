package com.rahulsmgv.parkinglot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActiveVehicleResponse {

    private String ticketId;

    private String vehicleNumber;

    private Long spotId;

    private LocalDateTime entryTime;
}