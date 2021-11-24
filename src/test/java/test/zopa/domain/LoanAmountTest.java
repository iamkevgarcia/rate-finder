package test.zopa.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LoanAmountTest {

    @Test
    public void itChecksAmountCanOnlyBeFrom1000To15000Inclusive() {
        assertThrows(InvalidLoanAmountError.class, () -> new LoanAmount(900));
        assertThrows(InvalidLoanAmountError.class, () -> new LoanAmount(15100));
    }

    @Test
    public void itChecksLoanAmountCanOnlyHave100Increments() {
        assertThrows(InvalidLoanAmountError.class, () -> new LoanAmount(1001));
        assertThrows(InvalidLoanAmountError.class, () -> new LoanAmount(14999));
    }
}
