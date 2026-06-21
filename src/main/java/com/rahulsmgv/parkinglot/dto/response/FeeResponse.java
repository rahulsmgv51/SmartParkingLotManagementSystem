package com.rahulsmgv.parkinglot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeeResponse {

    private String ticketId;

    private String vehicleNumber;

    private double parkingFee;

    private LocalDateTime entryTime;
}