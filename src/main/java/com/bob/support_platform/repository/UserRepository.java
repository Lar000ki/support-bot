package com.bob.support_platform.repository;

import com.bob.support_platform.core.model.PlatformType;
import com.bob.support_platform.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPlatformAndExternalId(
            PlatformType platform,
            long externalId
    );
}
