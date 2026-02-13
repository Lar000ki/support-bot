package com.bob.support_platform.core;

import com.bob.support_platform.core.dto.CoreCommand;
import com.bob.support_platform.core.interfaces.CoreResponse;
import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.service.TextService;
import com.bob.support_platform.core.service.TicketService;
import com.bob.support_platform.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreCommandProcessor {

    private final UserService userService;
    private final TextService textService;
    private final TicketService ticketService;

    public List<CoreResponse> handle(CoreCommand cmd) {

        return switch (cmd.type()) {

            case START -> handleStart(cmd);
            case BAN -> handleBan(cmd);
            case UNBAN -> handleUnban(cmd);
            case CLOSE -> handleClose(cmd);
            case REOPEN -> handleReopen(cmd);
            case STATUS -> handleStatus(cmd);
            case HELP -> handleHelp(cmd);
        };
    }
    private List<CoreResponse> handleStart(CoreCommand cmd) {
        return List.of(new CoreResponse.SendText(cmd.chatId(), textService.get("start")));
    }

    private List<CoreResponse> handleBan(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/ban " + textService.get("user-id"));
        }

        long userId = parseLong(cmd.args().get(0));
        if (userId == -1) {
            return usage(cmd, "/ban " + textService.get("user-id"));
        }

        boolean found = userService.setBanned(cmd.platform(), userId, true);

        String text = found
                ? textService.get("user") + " " + userId + " " + textService.get("banned")
                : textService.get("user-not-found");

        return List.of(new CoreResponse.SendText(cmd.chatId(), text));
    }

    private List<CoreResponse> handleUnban(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/unban " + textService.get("user-id"));
        }

        long userId = parseLong(cmd.args().get(0));
        if (userId == -1) {
            return usage(cmd, "/unban " + textService.get("user-id"));
        }

        boolean found = userService.setBanned(cmd.platform(), userId, false);

        String text = found
                ? textService.get("user") + " " + userId + " " + textService.get("unbanned")
                : textService.get("user-not-found");

        return List.of(new CoreResponse.SendText(cmd.chatId(), text));
    }

    private List<CoreResponse> handleClose(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/close " + textService.get("ticket") + " id");
        }

        long ticketId = parseLong(cmd.args().get(0));
        if (ticketId == -1) {
            return usage(cmd, "/close " + textService.get("ticket") + " id");
        }

        Ticket ticket = ticketService.getTicket(ticketId);

        if (ticket == null) {
            return List.of(
                    new CoreResponse.SendText(
                            cmd.chatId(),
                            textService.get("ticket-not-found")
                    )
            );
        }

        if (!ticket.isOpen()) {
            return List.of(
                    new CoreResponse.SendText(
                            cmd.chatId(),
                            textService.get("ticket-already-closed")
                    )
            );
        }

        ticketService.close(ticket);

        return List.of(
                new CoreResponse.SendText(
                        cmd.chatId(),
                        textService.get("ticket") + " #" + ticketId + " " + textService.get("ticket-closed")
                )
        );
    }

    private List<CoreResponse> handleReopen(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/reopen " + textService.get("ticket") + " id");
        }

        long ticketId = parseLong(cmd.args().get(0));
        if (ticketId == -1) {
            return usage(cmd, "/reopen " + textService.get("ticket") + " id");
        }

        Ticket ticket = ticketService.getTicket(ticketId);

        if (ticket == null) {
            return List.of(
                    new CoreResponse.SendText(
                            cmd.chatId(),
                            textService.get("ticket-not-found")
                    )
            );
        }

        if (ticket.isOpen()) {
            return List.of(
                    new CoreResponse.SendText(
                            cmd.chatId(),
                            textService.get("ticket-already-open")
                    )
            );
        }

        ticketService.reopen(ticket);

        return List.of(
                new CoreResponse.SendText(
                        cmd.chatId(),
                        textService.get("ticket") + " #" + ticketId + " " + textService.get("ticket-reopened")
                )
        );
    }

    private List<CoreResponse> handleStatus(CoreCommand cmd) {
        if (cmd.args().size() != 1) {
            return usage(cmd, "/status " + textService.get("ticket") + " id");
        }

        long ticketId = parseLong(cmd.args().get(0));
        if (ticketId == -1) {
            return usage(cmd, "/status " + textService.get("ticket") + " id");
        }

        Ticket ticket = ticketService.getTicketWithUser(ticketId);

        if (ticket == null) {
            return List.of(
                    new CoreResponse.SendText(
                            cmd.chatId(),
                            textService.get("ticket-not-found")
                    )
            );
        }

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault());

        String status = ticket.isOpen()
                ? textService.get("status-open")
                : textService.get("status-closed");

        String info = """
                %s #%d
                %s: %s
                %s: %d
                %s: %s
                """.formatted(
                textService.get("ticket"),
                ticket.getId(),
                textService.get("status"),
                status,
                textService.get("user-id"),
                ticket.getUser().getExternalId(),
                textService.get("created-at"),
                formatter.format(ticket.getCreatedAt())
        );

        return List.of(new CoreResponse.SendText(cmd.chatId(), info));
    }

    private List<CoreResponse> handleHelp(CoreCommand cmd) {
        String adminCommands = Arrays.stream(CoreCommandType.values())
                .filter(c -> c.getScope() == com.bob.support_platform.core.model.CommandScope.ADMIN)
                .map(c -> "/" + c.getName())
                .collect(Collectors.joining(", "));

        String helpText = textService.get("help-commands") + ": " + adminCommands;

        return List.of(new CoreResponse.SendText(cmd.chatId(), helpText));
    }

    private List<CoreResponse> usage(CoreCommand cmd, String usage) {
        return List.of(
                new CoreResponse.SendText(cmd.chatId(), textService.get("usage-command") + " " + usage)
        );
    }

    private long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }
}

