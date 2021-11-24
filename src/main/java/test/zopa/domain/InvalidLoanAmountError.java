package test.zopa.domain;

public final class InvalidLoanAmountError extends RuntimeException {
    public InvalidLoanAmountError(String message) {
        super(message);
    }
}
