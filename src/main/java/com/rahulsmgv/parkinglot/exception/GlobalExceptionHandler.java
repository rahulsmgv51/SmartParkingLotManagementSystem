package com.rahulsmgv.parkinglot.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle expected parking domain exceptions and return a clean API response.
     */
    @ExceptionHandler(ParkingException.class)
    public ResponseEntity<?> handleParkingException(ParkingException ex) {
        log.error("ParkingException handled: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("timestamp", LocalDateTime.now(), "message", ex.getMessage()));
    }

}