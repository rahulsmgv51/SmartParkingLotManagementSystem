package com.rahulsmgv.parkinglot.entity;

import com.rahulsmgv.parkinglot.enums.TicketStatus;
import com.rahulsmgv.parkinglot.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

        @Id
        @Column(name = "ticket_id")
        private String ticketId;

        @Column(name = "vehicle_number", nullable = false)
        private String vehicleNumber;

        @Enumerated(EnumType.STRING)
        @Column(name = "vehicle_type", nullable = false)
        private VehicleType vehicleType;

        @Column(name = "spot_id", nullable = false)
        private Long spotId;

        @Column(name = "entry_time", nullable = false)
        private LocalDateTime entryTime;

        @Column(name = "exit_time")
        private LocalDateTime exitTime;

        private Double amount;

        @Enumerated(EnumType.STRING)
        private TicketStatus status;
}