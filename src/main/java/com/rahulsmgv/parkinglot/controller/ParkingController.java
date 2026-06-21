package com.rahulsmgv.parkinglot.controller;

import com.rahulsmgv.parkinglot.dto.request.EntryRequest;
import com.rahulsmgv.parkinglot.dto.request.ExitRequest;
import com.rahulsmgv.parkinglot.dto.response.EntryResponse;
import com.rahulsmgv.parkinglot.dto.response.ExitResponse;
import com.rahulsmgv.parkinglot.service.ParkingService;

import jakarta.validation.Valid;

import com.rahulsmgv.parkinglot.util.TracingConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class ParkingController {

        private final ParkingService parkingService;

        /*
         * Vehicle Entry API
         */
        @PostMapping("/entry")
        public ResponseEntity<EntryResponse> parkVehicle(
                        @Valid @RequestBody EntryRequest request) {

                String correlationId = MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY);
                String userId = MDC.get(TracingConstants.MDC_USER_ID_KEY);
                String sessionId = MDC.get(TracingConstants.MDC_SESSION_ID_KEY);
                log.info("Step 1 - Received parking entry request: vehicleNumber={}, vehicleType={}, correlationId={}, sessionId={}, userId={}",
                                request.getVehicleNumber(),
                                request.getVehicleType(),
                                correlationId,
                                sessionId,
                                userId);

                log.info("Step 2 - Invoking parkVehicle service for vehicleNumber={}", request.getVehicleNumber());
                EntryResponse response = parkingService.parkVehicle(request);

                log.info("Step 3 - Parking entry completed: ticketId={}, spotNo={}, entryTime={}, correlationId={}",
                                response.getTicketId(),
                                response.getSpotNo(),
                                response.getEntryTime(),
                                correlationId);

                return ResponseEntity.ok(response);
        }

        @PostMapping("/exit")
        public ResponseEntity<ExitResponse> exitVehicle(
                        @Valid @RequestBody ExitRequest request) {

                String correlationId = MDC.get(TracingConstants.MDC_CORRELATION_ID_KEY);
                String userId = MDC.get(TracingConstants.MDC_USER_ID_KEY);
                String sessionId = MDC.get(TracingConstants.MDC_SESSION_ID_KEY);
                log.info("Step 1 - Received parking exit request: ticketId={}, correlationId={}, sessionId={}, userId={}",
                                request.getTicketId(),
                                correlationId,
                                sessionId,
                                userId);

                log.info("Step 2 - Invoking exitVehicle service for ticketId={}", request.getTicketId());
                ExitResponse response = parkingService.exitVehicle(request);

                log.info("Step 3 - Parking exit completed: ticketId={}, vehicleNumber={}, parkingFee={}, exitTime={}, correlationId={}",
                                response.getTicketId(),
                                response.getVehicleNumber(),
                                response.getParkingFee(),
                                response.getExitTime(),
                                correlationId);

                return ResponseEntity.ok(response);
        }
}