package com.bob.support_platform.core.dto;

import com.bob.support_platform.core.model.PlatformType;

import java.util.List;

public record CoreCommand(
        PlatformType platform,
        long adminId,
        String name,
        List<String> args
) {}
