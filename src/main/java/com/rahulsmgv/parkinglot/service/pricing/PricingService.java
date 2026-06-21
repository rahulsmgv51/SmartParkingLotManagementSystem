package com.rahulsmgv.parkinglot.service.pricing;

import com.rahulsmgv.parkinglot.entity.Ticket;

public interface PricingService {

    double calculateFee(Ticket ticket);
}