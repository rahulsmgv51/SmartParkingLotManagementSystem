package com.rahulsmgv.parkinglot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvailableSlotsResponse {

    private Long totalAvailableSpots;

    private Long availableCarSpots;

    private Long availableBikeSpots;

    private Long availableTruckSpots;
}