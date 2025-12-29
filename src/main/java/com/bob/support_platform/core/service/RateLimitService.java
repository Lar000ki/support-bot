package com.bob.support_platform.core.service;


import com.bob.support_platform.config.SupportProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final SupportProperties properties;

    private final Map<UUID, List<Instant>> userMessages = new ConcurrentHashMap<>();
    private final Map<UUID, Instant> restrictedUntil = new ConcurrentHashMap<>();

    public boolean isAllowed(UUID userId) {

        Instant now = Instant.now();

        if (restrictedUntil.containsKey(userId)
                && restrictedUntil.get(userId).isAfter(now)) {
            return false;
        }

        userMessages.putIfAbsent(userId, new ArrayList<>());
        List<Instant> messages = userMessages.get(userId);

        messages.removeIf(
                time -> time.isBefore(
                        now.minusSeconds(properties.getMessages().getLimitTime())
                )
        );

        if (messages.size() >= properties.getMessages().getLimitAmountmsg()) {
            restrictedUntil.put(
                    userId,
                    now.plusSeconds(properties.getMessages().getLimitRestrictTime())
            );
            messages.clear();
            return false;
        }

        messages.add(now);
        return true;
    }
}

