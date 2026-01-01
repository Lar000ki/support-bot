package com.bob.support_platform.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TextService {

    private final Environment environment;

    public String get(String key) {
        return environment.getProperty(
                "support.texts." + key,
                "[missing: " + key + "]"
        );
    }

    public String format(String key, Map<String, Object> params) {
        String text = get(key);
        for (var entry : params.entrySet()) {
            text = text.replace(
                    "{" + entry.getKey() + "}",
                    String.valueOf(entry.getValue())
            );
        }
        return text;
    }
}

