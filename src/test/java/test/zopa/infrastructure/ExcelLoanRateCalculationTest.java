package test.zopa.infrastructure;

import org.junit.jupiter.api.Test;
import test.zopa.domain.LoanAmount;
import test.zopa.domain.LoanRateCalculation;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcelLoanRateCalculationTest {

    @Test
    public void itFindsAnnualRate() {
        LoanRateCalculation rateCalculation = new ExcelLoanRateCalculation();
        BigDecimal foundRate = rateCalculation.findAnnual(36, BigDecimal.valueOf(30.88), new LoanAmount(1000));

        assertEquals(foundRate.toString(), "0.070063494139173696");
    }
}
