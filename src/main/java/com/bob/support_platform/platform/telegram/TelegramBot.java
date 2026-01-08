package com.bob.support_platform.platform.telegram;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramProperties tgProperties;
    private final TelegramUpdateHandler tgHandler;

    @PostConstruct
    public void init() {
        log.info("TG BOT enabled={}",
                tgProperties.isEnabled()
        );
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!tgProperties.isEnabled()) return;
        tgHandler.handle(update, this);
    }

    @Override
    public String getBotToken() {
        return tgProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return tgProperties.getUsername();
    }
}





