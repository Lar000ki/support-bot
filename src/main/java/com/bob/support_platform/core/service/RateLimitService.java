package com.bob.support_platform.core.service;


import com.bob.support_platform.config.SupportProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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

        Instant restrictedTime = restrictedUntil.get(userId);
        if (restrictedTime != null) {
            if (restrictedTime.isAfter(now)) {
                return false;
            }
            restrictedUntil.remove(userId);
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

    @Scheduled(fixedDelay = 3600000)
    public void cleanup() {
        Instant now = Instant.now();
        long windowSeconds = properties.getMessages().getLimitTime();

        userMessages.entrySet().removeIf(entry -> {
            List<Instant> msgs = entry.getValue();
            msgs.removeIf(time -> time.isBefore(now.minusSeconds(windowSeconds)));
            return msgs.isEmpty();
        });

        restrictedUntil.entrySet().removeIf(entry ->
                entry.getValue().isBefore(now)
        );
    }
}

