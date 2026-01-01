package com.bob.support_platform.platform.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "support.platforms.telegram")
public class TelegramProperties {

    private boolean enabled;
    private String token;
    private long supportChatId;
}
