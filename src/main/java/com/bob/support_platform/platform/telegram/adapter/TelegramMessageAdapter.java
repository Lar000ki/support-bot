package com.bob.support_platform.platform.telegram.adapter;

import com.bob.support_platform.core.interfaces.PlatformMessage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TelegramMessageAdapter {

    public PlatformMessage adapt(Message message) {
        return new TelegramPlatformMessage(message);
    }
}

