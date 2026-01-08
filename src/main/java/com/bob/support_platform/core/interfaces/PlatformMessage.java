package com.bob.support_platform.core.interfaces;

import com.bob.support_platform.core.model.PlatformType;

public interface PlatformMessage {
    PlatformType platform();
    long chatId();
    long senderId();
    String text();
    boolean hasAttachments();
    Object nativeMessage();
}
