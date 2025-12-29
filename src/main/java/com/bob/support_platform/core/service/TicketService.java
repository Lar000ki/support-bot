package com.bob.support_platform.core.service;

import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.model.TicketStatus;
import com.bob.support_platform.core.model.User;
import com.bob.support_platform.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket getOrCreateOpenTicket(User user) {

        return ticketRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(
                        user,
                        TicketStatus.OPEN
                )
                .orElseGet(() -> {
                    Ticket ticket = new Ticket();
                    ticket.setUser(user);
                    ticket.setStatus(TicketStatus.OPEN);
                    ticket.setCreatedAt(Instant.now());
                    ticket.setLastActivityAt(Instant.now());
                    return ticketRepository.save(ticket);
                });
    }

    public void touch(Ticket ticket) {
        ticket.setLastActivityAt(Instant.now());
        ticketRepository.save(ticket);
    }

    public void close(Ticket ticket) {
        ticket.setStatus(TicketStatus.CLOSED);
        ticketRepository.save(ticket);
    }
}

