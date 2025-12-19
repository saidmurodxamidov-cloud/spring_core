package org.example.security;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.metrics.MetricsService;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MetricsService metricsService;
    private final BruteForceProtectionService bruteForceProtectionService;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Timer.Sample sample = metricsService.startLoginTimer();
        try{
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("Authenticating user: {}", username);

        // Check if user is blocked due to brute force protection
        if (bruteForceProtectionService.isBlocked(username)) {
            long remainingSeconds = bruteForceProtectionService.getRemainingBlockTimeSeconds(username);
            log.warn("Login attempt blocked for user {} due to brute force protection. Remaining time: {} seconds", 
                username, remainingSeconds);
            metricsService.incrementLoginFailure();
            throw new BadCredentialsException(
                String.format("Account temporarily locked due to multiple failed login attempts. Please try again in %d seconds.", 
                    remainingSeconds)
            );
        }

        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    bruteForceProtectionService.recordFailedLogin(username);
                    metricsService.incrementLoginFailure();
                    log.error("User not found: {}", username);
                    return new BadCredentialsException("Invalid username or password");
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            boolean isBlocked = bruteForceProtectionService.recordFailedLogin(username);
            log.error("Invalid password for user: {}", username);
            metricsService.incrementLoginFailure();
            
            if (isBlocked) {
                long remainingSeconds = bruteForceProtectionService.getRemainingBlockTimeSeconds(username);
                throw new BadCredentialsException(
                    String.format("Account temporarily locked due to multiple failed login attempts. Please try again in %d seconds.", 
                        remainingSeconds)
                );
            }
            throw new BadCredentialsException("Invalid username or password");
        }
        
        // Successful login - reset brute force protection
        bruteForceProtectionService.recordSuccessfulLogin(username);

        // getRoles() returns List<Role>, which implements GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> (GrantedAuthority) role)
                .collect(Collectors.toList());

        log.info("User authenticated: {} with roles: {}", username,
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        metricsService.incrementLoginSuccess();
        metricsService.incrementActiveUsers();
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
        } finally {
            metricsService.recordLoginDuration(sample);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}