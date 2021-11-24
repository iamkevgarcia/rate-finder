package test.zopa.infrastructure.persistence;

public final class InvalidCSVMarketOffer extends RuntimeException {
    public InvalidCSVMarketOffer(String message) {
        super(message);
    }
}
