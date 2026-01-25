package com.bob.support_platform.core;

import com.bob.support_platform.config.SupportProperties;
import com.bob.support_platform.core.dto.AdminReplyContext;
import com.bob.support_platform.core.interfaces.CoreResponse;
import com.bob.support_platform.core.interfaces.PlatformMessage;
import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.service.RateLimitExceededException;
import com.bob.support_platform.core.service.SupportService;
import com.bob.support_platform.core.service.TextService;
import com.bob.support_platform.core.service.UserBannedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreSupportProcessor {

    private final SupportService supportService;
    private final TextService textService;
    private final SupportProperties supportProperties;
    private final PlatformConfigResolver platformConfigResolver;

    public List<CoreResponse> handleUserMessage(PlatformMessage msg) {

        List<CoreResponse> responses = new ArrayList<>();

        // Check message length
        int maxLength = supportProperties.getMessages().getMaxLength();
        if (msg.text() != null && msg.text().length() > maxLength) {
            return List.of(
                    new CoreResponse.SendText(
                            msg.chatId(),
                            textService.get("message-too-long")
                    )
            );
        }

        Ticket ticket;
        try {
            ticket = supportService.onUserMessage(
                    msg.platform(),
                    msg.senderId()
            );
        } catch (UserBannedException e) {
            return List.of(CoreResponse.Ignore.INSTANCE);
        } catch (RateLimitExceededException e) {
            return List.of(
                    new CoreResponse.SendText(
                            msg.chatId(),
                            textService.get("rate-limit")
                    )
            );
        }



        // Greeting
        if (ticket.getCreatedAt().equals(ticket.getLastActivityAt())
                && supportProperties.getMessages().isGreetingEnabled()) {

            responses.add(
                    new CoreResponse.SendText(
                            msg.chatId(),
                            textService.get("greeting")
                    )
            );

            supportService.touchTicket(ticket);
        }

        // Служебный текст в саппорт
        String supportText = buildSupportText(
                ticket,
                msg.senderId(),
                msg.text()
        );

        long supportChatId =
                platformConfigResolver.getSupportChatId(msg.platform());

        responses.add(
                new CoreResponse.SendText(
                        supportChatId,
                        supportText
                )
        );

        // Копирование вложений
        if (msg.hasAttachments()) {
            responses.add(
                    new CoreResponse.CopyMessage(
                            msg.chatId(),
                            supportChatId,
                            msg.nativeMessage()
                    )
            );
        }

        return responses;
    }

    private String buildSupportText(
            Ticket ticket,
            long externalUserId,
            String originalText
    ) {
        return """
        %s
        %s: #%d
        %s: %d

        %s
        """.formatted(
                textService.get("newmsg"),
                textService.get("ticket"),
                ticket.getId(),
                textService.get("user-id"),
                externalUserId,
                originalText == null ? "" : originalText
        );
    }

    public List<CoreResponse> handleAdminReply(AdminReplyContext ctx) {

        PlatformMessage msg = ctx.adminMessage();
        long ticketId = ctx.ticketId();

        List<CoreResponse> responses = new ArrayList<>();

        Ticket ticket = supportService.getTicketWithUser(ticketId);

        if (ticket == null || !ticket.isOpen()) {
            return List.of(CoreResponse.Ignore.INSTANCE);
        }

        long userChatId = ticket.getUser().getExternalId();

        boolean textOnly =
                msg.text() != null && !msg.hasAttachments();

        if (textOnly) {
            String text = applyHeaderFooter(msg.text());
            responses.add(
                    new CoreResponse.SendText(userChatId, text)
            );
        } else {
            responses.add(
                    new CoreResponse.CopyMessage(
                            msg.chatId(),
                            userChatId,
                            msg.nativeMessage()
                    )
            );
        }

        supportService.onAgentReply(ticket);

        return responses;
    }

    private String applyHeaderFooter(String original) {

        String text = original;

        if (supportProperties.getMessages().isHeaderEnabled()) {
            text = textService.get("header") + "\n" + text;
        }

        if (supportProperties.getMessages().isFooterEnabled()) {
            text = text + "\n" + textService.get("footer");
        }

        return text;
    }

}


