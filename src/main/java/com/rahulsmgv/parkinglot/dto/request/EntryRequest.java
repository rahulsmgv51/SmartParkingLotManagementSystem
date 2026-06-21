package com.rahulsmgv.parkinglot.dto.request;

import com.rahulsmgv.parkinglot.enums.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EntryRequest {

    // Vehicle registration number
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    // Vehicle category
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;
}