package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.persistence.entity.Role;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@ConditionalOnProperty(name = "prometheus.security.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class PrometheusSecurityConfig {

    private final PrometheusSecurityProperties prometheusSecurityProperties;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService prometheusUserDetailsService() {
        UserDetails user = User.builder()
                .username(prometheusSecurityProperties.getUsername())
                .password(passwordEncoder.encode(prometheusSecurityProperties.getPassword()))
                .roles(Role.PROMETHEUS.name())
                .build();
        
        return new InMemoryUserDetailsManager(user);
    }
}

