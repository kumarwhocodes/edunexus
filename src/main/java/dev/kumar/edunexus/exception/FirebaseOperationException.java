package dev.kumar.edunexus.exception;

public class FirebaseOperationException extends RuntimeException {
    public FirebaseOperationException(String message) {
        super(message);
    }
}