package com.rahulsmgv.parkinglot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExitRequest {

    // Parking ticket identifier
    @NotBlank
    private String ticketId;

    // Vehicle registration number, used when ticketId is not supplied.
    private String vehicleNumber;
}