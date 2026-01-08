package com.bob.support_platform.platform.telegram.adapter;

import com.bob.support_platform.core.dto.CoreCommand;
import com.bob.support_platform.core.model.PlatformType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class TelegramCommandAdapter {

    public CoreCommand adapt(Message message) {

        String text = message.getText().trim();
        String[] parts = text.split("\\s+");

        String name = parts[0].substring(1);
        List<String> args = List.of(parts).subList(1, parts.length);

        return new CoreCommand(
                PlatformType.TELEGRAM,
                message.getChatId(),
                name,
                args
        );
    }
}

