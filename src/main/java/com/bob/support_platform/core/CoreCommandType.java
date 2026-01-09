package com.bob.support_platform.core;

import com.bob.support_platform.core.model.CommandScope;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum CoreCommandType {

    START("start", CommandScope.USER),
    BAN("ban", CommandScope.ADMIN),
    UNBAN("unban", CommandScope.ADMIN);

    private final String name;
    private final CommandScope scope;

    CoreCommandType(String name, CommandScope scope) {
        this.name = name;
        this.scope = scope;
    }

    public static Optional<CoreCommandType> from(String raw) {
        return Arrays.stream(values()).filter(c -> c.name.equals(raw)).findFirst();
    }
}

