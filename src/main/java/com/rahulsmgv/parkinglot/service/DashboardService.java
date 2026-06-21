package com.rahulsmgv.parkinglot.service;

import java.util.List;

import com.rahulsmgv.parkinglot.dto.response.ActiveVehicleResponse;
import com.rahulsmgv.parkinglot.dto.response.DashboardResponse;

public interface DashboardService {

    DashboardResponse getSummary();

    List<ActiveVehicleResponse> getActiveVehicles();
}