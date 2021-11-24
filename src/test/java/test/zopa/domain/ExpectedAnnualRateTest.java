package test.zopa.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ExpectedAnnualRateTest {

    @Test
    public void itThrowsErrorWhenRateIsNotGreaterThanZero() {
        assertThrows(InvalidRateError.class, () -> new AnnualInterestRate(BigDecimal.valueOf(0.00)));
    }
}
