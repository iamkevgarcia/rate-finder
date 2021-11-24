package test.zopa.domain;

public final class NotEnoughMarketOffersError extends RuntimeException {
    public NotEnoughMarketOffersError(String message) {
        super(message);
    }
}
