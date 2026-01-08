package com.bob.support_platform.core.dto;

import com.bob.support_platform.core.interfaces.PlatformMessage;

public record AdminReplyContext(
        PlatformMessage adminMessage,
        long ticketId
) {}

