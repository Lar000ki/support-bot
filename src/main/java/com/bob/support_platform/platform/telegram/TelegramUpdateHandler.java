package com.bob.support_platform.platform.telegram;

import com.bob.support_platform.config.SupportProperties;
import com.bob.support_platform.core.model.PlatformType;
import com.bob.support_platform.core.model.Ticket;
import com.bob.support_platform.core.service.RateLimitExceededException;
import com.bob.support_platform.core.service.SupportService;
import com.bob.support_platform.core.service.TextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TG <--> CORE связь
@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdateHandler {

    private final SupportService supportService;
    private final TelegramSender sender;
    private final TelegramProperties tgProperties;
    private final SupportProperties supportProperties;
    private final TextService textService;

    public void handle(Update update, TelegramLongPollingBot bot) {
        if (!update.hasMessage()) return;

        Message message = update.getMessage();
        if (isAdminReply(message)) {
            handleAdminReply(message, bot);
            return;
        }
        if (message.isUserMessage()) {
            handleUserMessage(message, bot);
            return;
        }
    }

    //юзер -> саппорт
    private void handleUserMessage(Message message, TelegramLongPollingBot bot) {

        Long externalUserId = message.getFrom().getId();

        Ticket ticket;
        try {
            ticket = supportService.onUserMessage(
                    PlatformType.TELEGRAM,
                    externalUserId
            );
        } catch (RateLimitExceededException e) {
            sender.sendText(bot, message.getChatId(), textService.get("rate-limit"));
            return;
        }

        // greeting
        if (ticket.getCreatedAt().equals(ticket.getLastActivityAt())
                && supportProperties.getMessages().isGreetingEnabled()) {
            sender.sendText(bot, message.getChatId(), textService.get("greeting"));
        }

        // 🔹 гибрид user -> support
        if (canRebuildTextMessage(message)) {
            String text = buildSupportText(message.getText(), ticket.getId());

            sender.sendText(bot, tgProperties.getSupportChatId(), text);

        } else {
            String text = buildSupportText("", ticket.getId());

            sender.sendText(bot, tgProperties.getSupportChatId(), text);
            sender.copyMessage(bot, message.getChatId(), tgProperties.getSupportChatId(), message.getMessageId());
        }
    }


    //саппорт -> юзер
    private void handleAdminReply(Message message, TelegramLongPollingBot bot) {

        if (!isValidAdminReply(message)) {
            return;
        }

        Message replied = message.getReplyToMessage();
        Long ticketId = extractTicketId(replied.getText());
        if (ticketId == null) {
            log.debug("ticketId not found");
            return;
        }
        Ticket ticket = supportService.getTicketWithUser(ticketId);
        if (ticket == null || !ticket.isOpen()) {
            log.debug("ticket not found or closed");
            return;
        }
        long userChatId = ticket.getUser().getExternalId();
        if (canRebuildTextMessage(message)) {
            sendFormattedTextReply(bot, userChatId, message.getText());
        } else {
            sender.copyMessage(
                    bot,
                    message.getChatId(),
                    userChatId,
                    message.getMessageId()
            );
        }
        supportService.onAgentReply(ticket);
    }


    private boolean isValidAdminReply(Message message) {
        return message.isReply()
                && message.getChatId().equals(tgProperties.getSupportChatId())
                && message.getReplyToMessage() != null
                && message.getReplyToMessage().getFrom() != null
                && Boolean.TRUE.equals(message.getReplyToMessage().getFrom().getIsBot())
                && message.getReplyToMessage().hasText();
    }

    private boolean canRebuildTextMessage(Message message) {
        return message.hasText()
                && !message.hasPhoto()
                && !message.hasDocument()
                && !message.hasVideo()
                && !message.hasVoice()
                && !message.hasAudio()
                && !message.hasSticker();
    }

    private void sendFormattedTextReply(
            TelegramLongPollingBot bot,
            long userChatId,
            String originalText
    ) {
        String text = originalText;

        if (supportProperties.getMessages().isHeaderEnabled()) {
            text = textService.get("header") + "\n" + text;
        }

        if (supportProperties.getMessages().isFooterEnabled()) {
            text = text + "\n" + textService.get("footer");
        }

        sender.sendText(bot, userChatId, text);
    }


    private boolean isAdminReply(Message message) {
        return message.isReply()
                && message.getChatId().equals(tgProperties.getSupportChatId());
    }

    private Long extractTicketId(String text) {
        Pattern pattern = Pattern.compile(textService.get("ticket") + ":\\s*#(\\d+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        return null;
    }

    private String buildSupportText(String originalText, long ticketId) {
        return """
        %s
        %s: #%d

        %s
        """.formatted(textService.get("newmsg"), textService.get("ticket"), ticketId, originalText);
    }

}


