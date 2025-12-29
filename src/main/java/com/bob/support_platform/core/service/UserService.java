package com.bob.support_platform.core.service;

import com.bob.support_platform.core.model.PlatformType;
import com.bob.support_platform.core.model.User;
import com.bob.support_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getOrCreate(
            PlatformType platform,
            String externalUserId
    ) {
        return userRepository
                .findByPlatformAndExternalId(platform, externalUserId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setPlatform(platform);
                    user.setExternalId(externalUserId);
                    return userRepository.save(user);
                });
    }
}

