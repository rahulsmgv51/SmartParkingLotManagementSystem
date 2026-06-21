package com.rahulsmgv.parkinglot.repository;

import com.rahulsmgv.parkinglot.entity.Ticket;
import com.rahulsmgv.parkinglot.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {

        /*
         * Find active ticket only
         */
        Optional<Ticket> findByTicketIdAndStatus(String ticketId, TicketStatus status);

        // NEW METHOD
        Optional<Ticket> findByVehicleNumberAndStatus( String vehicleNumber, TicketStatus status);

        long countByStatus(TicketStatus status);

        List<Ticket> findByStatus(TicketStatus status);
}