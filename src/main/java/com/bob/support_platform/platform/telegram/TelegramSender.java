package com.bob.support_platform.platform.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSender {

    private final TelegramProperties tgProperties;

    public void sendServiceMessage(
            TelegramLongPollingBot bot,
            Long ticketId,
            String externalUserId
    ) {
        SendMessage msg = new SendMessage();
        msg.setChatId(tgProperties.getSupportChatId());
        msg.setText("""
            New support message
            Ticket: #%d
            User ID: %s
            """.formatted(ticketId, externalUserId));

        execute(bot, msg);
    }

    public void sendText(
            TelegramLongPollingBot bot,
            Long chatId,
            String text
    ) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);

        execute(bot, msg);
    }

    public void copyMessage(
            TelegramLongPollingBot bot,
            Long fromChat,
            Long toChat,
            Integer messageId
    ) {
        CopyMessage copy = new CopyMessage();
        copy.setFromChatId(fromChat);
        copy.setChatId(toChat);
        copy.setMessageId(messageId);

        execute(bot, copy);
    }

    private <T extends Serializable, M extends BotApiMethod<T>>
    void execute(TelegramLongPollingBot bot, M method) {
        try {
            bot.execute(method);
        } catch (Exception e) {
            log.error("TG API error", e);
        }
    }
}
