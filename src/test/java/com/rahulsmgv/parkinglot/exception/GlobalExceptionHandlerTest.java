package com.rahulsmgv.parkinglot.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @Test
    void shouldHandleParkingException() {

        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ResponseEntity<?> response = handler.handleParkingException(
                new ParkingException("No Spot"));

        assertEquals(
                400,
                response.getStatusCode().value());
    }
}