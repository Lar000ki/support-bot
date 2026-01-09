package com.bob.support_platform.platform.telegram.adapter;

import com.bob.support_platform.core.CoreCommandType;
import com.bob.support_platform.core.dto.CoreCommand;
import com.bob.support_platform.core.model.PlatformType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramCommandAdapter {

    public Optional<CoreCommand> adapt(Message message) {

        if (!message.hasText()) return Optional.empty();
        if (!message.getText().startsWith("/")) return Optional.empty();

        String[] parts = message.getText().trim().split("\\s+");
        String rawName = parts[0].substring(1);

        Optional<CoreCommandType> type = CoreCommandType.from(rawName);

        if (type.isEmpty()) return Optional.empty();

        List<String> args = List.of(parts).subList(1, parts.length);

        return Optional.of(
                new CoreCommand(
                        PlatformType.TELEGRAM,
                        message.getChatId(),
                        type.get(),
                        args
                )
        );
    }
}


