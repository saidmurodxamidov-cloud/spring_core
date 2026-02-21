package org.example.security.service;

import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.example.metrics.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
@RequiredArgsConstructor
public class GymCustomersAuthenticationService implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(GymCustomersAuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MetricsService metricsService;
    private final BruteForceProtectionService bruteForceProtectionService;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Timer.Sample sample = metricsService.startLoginTimer();
        try {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            log.info("Authenticating user: {}", username);

            validateUserNotBlocked(username);
            UserEntity user = findUserByUsername(username);
            validatePassword(password, user);
            recordSuccessfulAuthentication(username);
            
            List<GrantedAuthority> authorities = extractAuthorities(user);
            logAuthenticationSuccess(username, authorities);
            
            return createAuthenticationToken(username, password, authorities);
        } finally {
            metricsService.recordLoginDuration(sample);
        }
    }

    private void validateUserNotBlocked(String username) {
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
    }

    private UserEntity findUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    handleAuthenticationFailure(username, "User not found");
                    return new BadCredentialsException("Invalid username or password");
                });
    }

    private void validatePassword(String password, UserEntity user) {
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            handleAuthenticationFailure(user.getUserName(), "Invalid password");
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    private void handleAuthenticationFailure(String username, String reason) {
        boolean isBlocked = bruteForceProtectionService.recordFailedLogin(username);
        log.error("{} for user: {}", reason, username);
        metricsService.incrementLoginFailure();
        
        if (isBlocked) {
            long remainingSeconds = bruteForceProtectionService.getRemainingBlockTimeSeconds(username);
            throw new BadCredentialsException(
                String.format("Account temporarily locked due to multiple failed login attempts. Please try again in %d seconds.", 
                    remainingSeconds)
            );
        }
    }

    private void recordSuccessfulAuthentication(String username) {
        bruteForceProtectionService.recordSuccessfulLogin(username);
        metricsService.incrementLoginSuccess();
        metricsService.incrementActiveUsers();
    }

    private List<GrantedAuthority> extractAuthorities(UserEntity user) {
        return user.getRoles().stream()
                .map(role -> (GrantedAuthority) role)
                .collect(Collectors.toList());
    }

    private void logAuthenticationSuccess(String username, List<GrantedAuthority> authorities) {
        log.info("User authenticated: {} with roles: {}", username,
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(
            String username, 
            String password, 
            List<GrantedAuthority> authorities) {
        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

