package test.zopa.infrastructure.persistence;

public final class FileIsNotACSVError extends RuntimeException {
    public FileIsNotACSVError(String message) {
        super(message);
    }
}
