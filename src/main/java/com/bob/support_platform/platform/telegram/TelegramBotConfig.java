package com.bob.support_platform.platform.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramBotConfig {

    private final TelegramBot telegramBot;
    private final TelegramProperties telegramProperties;

    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {

        if (!telegramProperties.isEnabled()) {
            log.info("TG disabled by config");
            return new TelegramBotsApi(DefaultBotSession.class);
        }

        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);

        log.info("TG bot registered");

        return api;
    }
}
