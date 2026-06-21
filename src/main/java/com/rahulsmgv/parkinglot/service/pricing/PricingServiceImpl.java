package com.rahulsmgv.parkinglot.service.pricing;

import com.rahulsmgv.parkinglot.entity.Ticket;
import com.rahulsmgv.parkinglot.enums.VehicleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PricingServiceImpl
        implements PricingService {

    private final CarFeeStrategy carFeeStrategy;
    private final BikeFeeStrategy bikeFeeStrategy;
    private final TruckFeeStrategy truckFeeStrategy;

    @Override
    public double calculateFee(
            Ticket ticket) {

        LocalDateTime exitTime = LocalDateTime.now();

        if (VehicleType.CAR.equals(
                ticket.getVehicleType())) {

            return carFeeStrategy.calculateFee(
                    ticket.getEntryTime(),
                    exitTime);
        }

        if (VehicleType.BIKE.equals(
                ticket.getVehicleType())) {

            return bikeFeeStrategy.calculateFee(
                    ticket.getEntryTime(),
                    exitTime);
        }

        return truckFeeStrategy.calculateFee(
                ticket.getEntryTime(),
                exitTime);
    }
}