package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Optional security configuration for Prometheus endpoint with basic authentication.
 * 
 * To enable basic auth for Prometheus:
 * 1. Set prometheus.security.enabled=true in application.yml
 * 2. Configure username and password (or use defaults: prometheus/prometheus)
 * 3. Update SecurityConfig to require authentication for /actuator/prometheus
 * 4. Update prometheus.yml to include basic_auth credentials
 * 
 * Example configuration in application.yml:
 * prometheus:
 *   security:
 *     enabled: true
 *     username: prometheus
 *     password: your_secure_password
 */
@Configuration
@ConditionalOnProperty(name = "prometheus.security.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class PrometheusSecurityConfig {

    private final Environment environment;

    @Bean
    public PasswordEncoder prometheusPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService prometheusUserDetailsService() {
        String username = environment.getProperty("prometheus.security.username", "prometheus");
        String password = environment.getProperty("prometheus.security.password", "prometheus");
        
        UserDetails user = User.builder()
                .username(username)
                .password(prometheusPasswordEncoder().encode(password))
                .roles("PROMETHEUS")
                .build();
        
        return new InMemoryUserDetailsManager(user);
    }
}

