package com.bob.support_platform.platform.telegram;

import com.bob.support_platform.core.model.PlatformType;
import com.bob.support_platform.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramCommandHandler {

    private final UserService userService;
    private final TelegramSender sender;
    private final TelegramProperties tgProperties;

    public boolean handle(Message message, TelegramLongPollingBot bot) {

        if (!message.hasText()) return false;
        if (!message.getChatId().equals(tgProperties.getSupportChatId())) return false;

        String text = message.getText().trim();

        if (text.startsWith("/ban ")) {
            handleBan(text, bot, message);
            return true;
        }

        if (text.startsWith("/unban ")) {
            handleUnban(text, bot, message);
            return true;
        }

        return false;
    }

    private void handleBan(String text, TelegramLongPollingBot bot, Message message) {
        Long userId = parseUserId(text);
        if (userId == null) {
            sender.sendText(bot, message.getChatId(), "usage: /ban userId");
            return;
        }

        userService.setBanned(PlatformType.TELEGRAM, userId, true);

        sender.sendText(
                bot,
                message.getChatId(),
                "user " + userId + " banned"
        );
    }

    private void handleUnban(String text, TelegramLongPollingBot bot, Message message) {
        Long userId = parseUserId(text);
        if (userId == null) {
            sender.sendText(bot, message.getChatId(), "usage: /unban userId");
            return;
        }

        userService.setBanned(PlatformType.TELEGRAM, userId, false);

        sender.sendText(
                bot,
                message.getChatId(),
                "user " + userId + " unbanned"
        );
    }

    private Long parseUserId(String text) {
        try {
            return Long.parseLong(text.split("\\s+")[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
