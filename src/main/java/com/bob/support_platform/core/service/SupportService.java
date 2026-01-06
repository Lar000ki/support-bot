package com.bob.support_platform.core.service;


import com.bob.support_platform.core.model.PlatformType;
import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final UserService userService;
    private final TicketService ticketService;
    private final RateLimitService rateLimitService;

    public Ticket onUserMessage(
            PlatformType platform,
            long externalUserId
    ) {
        User user = userService.getOrCreate(platform, externalUserId);

        if (!rateLimitService.isAllowed(user.getId())) {
            throw new RateLimitExceededException();
        }

        Ticket ticket = ticketService.getOrCreateOpenTicket(user);
        return ticket;
    }

    public Ticket getTicket(Long id) {
        return ticketService.getTicket(id);
    }

    public Ticket getTicketWithUser(Long id) {
        return ticketService.getTicketWithUser(id);
    }

    public void onAgentReply(Ticket ticket) {
        ticketService.onAgentReply(ticket);
    }
}
