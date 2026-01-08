package com.bob.support_platform.web;

import com.bob.support_platform.config.SupportProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final SupportProperties supportProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!supportProperties.getWeb().isEnabled()) {
            http
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().denyAll()
                    );
            return http.build();
        }
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/css/**", "/js/**")
                        .authenticated()
                        .requestMatchers("/api/admin/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        var admin = User.withUsername(
                        supportProperties.getWeb().getAdmin().getUsername()
                )
                .password("{noop}" + supportProperties.getWeb().getAdmin().getPassword())
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}


