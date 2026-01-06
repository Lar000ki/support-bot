package com.bob.support_platform.core.service;

import com.bob.support_platform.config.SupportProperties;
import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.model.TicketStatus;
import com.bob.support_platform.core.model.User;
import com.bob.support_platform.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SupportProperties supportProperties;

    public Ticket getOrCreateOpenTicket(User user) {

        Instant now = Instant.now();

        return ticketRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(
                        user,
                        TicketStatus.OPEN
                )
                .orElseGet(() -> {
                    Ticket ticket = new Ticket();
                    ticket.setUser(user);
                    ticket.setStatus(TicketStatus.OPEN);
                    ticket.setCreatedAt(now);
                    ticket.setLastActivityAt(now);
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

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketWithUser(Long id) {
        return ticketRepository.findByIdWithUser(id)
                .orElse(null);
    }


    public void onAgentReply(Ticket ticket) {
        touch(ticket);

        if (supportProperties.getTickets().isAutoCloseOnReply()) {
            close(ticket);
        }
    }
}

