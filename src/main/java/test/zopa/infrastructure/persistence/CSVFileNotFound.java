package test.zopa.infrastructure.persistence;

public final class CSVFileNotFound extends RuntimeException {
    public CSVFileNotFound(String message) {
        super(message);
    }
}
