package test.zopa.infrastructure.persistence;

public final class UnableToReadMarketOffersFromCSV extends RuntimeException {
    public UnableToReadMarketOffersFromCSV(String message, Throwable cause) {
        super(message, cause);
    }
}
