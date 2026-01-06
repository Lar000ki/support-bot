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

    public void sendText(TelegramLongPollingBot bot, long externalUserId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(externalUserId);
        msg.setText(text);

        execute(bot, msg);
    }

    public void copyMessage(TelegramLongPollingBot bot, long fromChat, long toChat, Integer messageId) {
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
