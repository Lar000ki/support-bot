package com.bob.support_platform.core.interfaces;

public sealed interface CoreResponse {

    record SendText(long chatId, String text) implements CoreResponse {}
    record CopyMessage(long from, long to, Object nativeMsg) implements CoreResponse {}
    enum Ignore implements CoreResponse {
        INSTANCE
    }
}

