package com.bob.support_platform.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SupportProperties.class)
public class SupportConfig {
}

