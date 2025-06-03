package ua.edu.networking.task1.exceptions;

public class ObjectSerializationError extends RuntimeException {
    public ObjectSerializationError(String message) {
        super(message);
    }

    public ObjectSerializationError(Throwable cause) {
        super(cause);
    }
}
