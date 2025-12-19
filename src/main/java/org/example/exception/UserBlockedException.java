package org.example.exception;

/**
 * Exception thrown when a user is blocked due to brute force protection
 */
public class UserBlockedException extends RuntimeException {
    
    private final long remainingBlockTimeSeconds;

    public UserBlockedException(String message, long remainingBlockTimeSeconds) {
        super(message);
        this.remainingBlockTimeSeconds = remainingBlockTimeSeconds;
    }

    public long getRemainingBlockTimeSeconds() {
        return remainingBlockTimeSeconds;
    }
}

