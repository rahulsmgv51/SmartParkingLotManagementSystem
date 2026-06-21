package com.rahulsmgv.parkinglot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {

    /*
     * Total parking capacity
     */
    private Long totalSpots;

    /*
     * Free spots
     */
    private Long availableSpots;

    /*
     * Occupied spots
     */
    private Long occupiedSpots;

    /*
     * Active vehicles
     */
    private Long activeVehicles;
}