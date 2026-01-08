package com.bob.support_platform.core;

import com.bob.support_platform.core.model.PlatformType;
import com.bob.support_platform.platform.telegram.TelegramProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PlatformConfigResolver {

    private final TelegramProperties telegramProperties;
    //private final DiscordProperties discordProperties;

    public long getSupportChatId(PlatformType platform) {
        return switch (platform) {
            case TELEGRAM -> telegramProperties.getSupportChatId();
            default -> throw new IllegalStateException(
                    "Unsupported platform: " + platform
            );
        };
    }
}

