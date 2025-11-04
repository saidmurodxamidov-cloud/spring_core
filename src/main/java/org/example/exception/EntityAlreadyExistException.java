package org.example.exception;

public class EntityAlreadyExistException extends RuntimeException {

    public EntityAlreadyExistException(String message) {
        super(message);
    }
    public EntityAlreadyExistException() {
        super();
    }
}

