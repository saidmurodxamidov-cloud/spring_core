package org.example.security.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class BruteForceProtectionService {

    private static final Logger log = LoggerFactory.getLogger(BruteForceProtectionService.class);

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 5;
    
    private final Map<String, LoginAttemptInfo> loginAttempts = new ConcurrentHashMap<>();

    /**
     * Records a failed login attempt
     * @param username the username that failed to login
     * @return true if the user should be blocked, false otherwise
     */
    public boolean recordFailedLogin(String username) {
        LoginAttemptInfo attemptInfo = loginAttempts.computeIfAbsent(
            username, 
            k -> new LoginAttemptInfo()
        );
        
        attemptInfo.incrementAttempts();
        attemptInfo.setLastAttemptTime(LocalDateTime.now());
        
        if (attemptInfo.getAttemptCount() >= MAX_LOGIN_ATTEMPTS) {
            attemptInfo.setBlockedUntil(
                LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES)
            );
            log.warn("User {} blocked due to {} failed login attempts. Blocked until {}", 
                username, attemptInfo.getAttemptCount(), attemptInfo.getBlockedUntil());
            return true;
        }
        
        log.debug("Failed login attempt {} for user {}", 
            attemptInfo.getAttemptCount(), username);
        return false;
    }

    /**
     * Records a successful login and resets the attempt counter
     * @param username the username that successfully logged in
     */
    public void recordSuccessfulLogin(String username) {
        loginAttempts.remove(username);
        log.debug("Successful login for user {}. Login attempts reset.", username);
    }

    /**
     * Checks if a user is currently blocked
     * @param username the username to check
     * @return true if the user is blocked, false otherwise
     */
    public boolean isBlocked(String username) {
        LoginAttemptInfo attemptInfo = loginAttempts.get(username);
        if (attemptInfo == null) {
            return false;
        }
        
        if (attemptInfo.getBlockedUntil() != null) {
            if (LocalDateTime.now().isBefore(attemptInfo.getBlockedUntil())) {
                log.warn("User {} is still blocked until {}", 
                    username, attemptInfo.getBlockedUntil());
                return true;
            } else {
                log.info("Block period expired for user {}. Resetting attempts.", username);
                loginAttempts.remove(username);
                return false;
            }
        }
        
        return false;
    }

    /**
     * Gets the remaining block time in seconds
     * @param username the username to check
     * @return remaining seconds until unblock, or 0 if not blocked
     */
    public long getRemainingBlockTimeSeconds(String username) {
        LoginAttemptInfo attemptInfo = loginAttempts.get(username);
        if (attemptInfo == null || attemptInfo.getBlockedUntil() == null) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(attemptInfo.getBlockedUntil())) {
            return java.time.Duration.between(now, attemptInfo.getBlockedUntil()).getSeconds();
        }
        
        return 0;
    }

    /**
     * Gets the number of failed attempts for a user
     * @param username the username to check
     * @return number of failed attempts
     */
    public int getFailedAttemptCount(String username) {
        LoginAttemptInfo attemptInfo = loginAttempts.get(username);
        return attemptInfo != null ? attemptInfo.getAttemptCount() : 0;
    }

    @Getter
    private static class LoginAttemptInfo {
        private int attemptCount = 0;
        @Setter
        private LocalDateTime lastAttemptTime;
        @Setter
        private LocalDateTime blockedUntil;

        public void incrementAttempts() {
            this.attemptCount++;
        }

    }
}

