package com.bob.support_platform.platform.telegram.adapter;

import com.bob.support_platform.core.interfaces.PlatformMessage;
import com.bob.support_platform.core.model.PlatformType;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@RequiredArgsConstructor
public class TelegramPlatformMessage implements PlatformMessage {

    private final Message message;

    @Override
    public PlatformType platform() { return PlatformType.TELEGRAM; }

    @Override
    public long chatId() { return message.getChatId(); }

    @Override
    public long senderId() { return message.getFrom().getId(); }

    @Override
    public String text() { return message.getText(); }

    @Override
    public boolean hasAttachments() {
        return message.hasPhoto() || message.hasDocument() || message.hasVideo() || message.hasVoice() || message.hasAudio() || message.hasSticker();
    }

    @Override
    public Object nativeMessage() {
        return message;
    }
}
