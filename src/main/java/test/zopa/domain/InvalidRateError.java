package test.zopa.domain;

public final class InvalidRateError extends RuntimeException {
    public InvalidRateError(String message) {
        super(message);
    }
}
