package com.rahulsmgv.parkinglot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExitResponse {

    private String ticketId;

    private String vehicleNumber;

    private Double parkingFee;

    private LocalDateTime entryTime;

    private LocalDateTime exitTime;
}