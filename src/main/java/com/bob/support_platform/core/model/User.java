package com.bob.support_platform.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
@UniqueConstraint(columnNames = {"platform", "external_id"})
}
)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformType platform;

    @Column(name = "external_id", nullable = false)
    private long externalId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}

