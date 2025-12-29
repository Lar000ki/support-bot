package com.bob.support_platform.repository;

import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.model.TicketStatus;
import com.bob.support_platform.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findFirstByUserAndStatusOrderByCreatedAtDesc(
            User user,
            TicketStatus status
    );
}

