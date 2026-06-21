package com.rahulsmgv.parkinglot.service;

import com.rahulsmgv.parkinglot.dto.request.EntryRequest;
import com.rahulsmgv.parkinglot.dto.request.ExitRequest;
import com.rahulsmgv.parkinglot.dto.response.EntryResponse;
import com.rahulsmgv.parkinglot.dto.response.ExitResponse;

public interface ParkingService {

    /*
     * Allocate parking spot and create ticket
     */
    EntryResponse parkVehicle(EntryRequest request);

    ExitResponse exitVehicle(ExitRequest request);

    double calculateFee(ExitRequest request);
}