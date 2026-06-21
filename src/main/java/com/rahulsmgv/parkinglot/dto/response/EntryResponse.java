package com.rahulsmgv.parkinglot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import jakarta.persistence.Column;

@Data
@Builder
public class EntryResponse {

    // Generated parking ticket id
    private String ticketId;

    // Allocated parking spot
    @Column(name = "spot_no", unique = true, nullable = false)
    private String spotNo;

    // Vehicle entry timestamp
    private LocalDateTime entryTime;
}