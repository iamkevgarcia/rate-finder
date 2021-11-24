package test.zopa.domain;

import java.math.BigDecimal;

public interface LoanRateCalculation {

    BigDecimal findAnnual(Integer numberOfPayments, BigDecimal monthly, LoanAmount amount);

    BigDecimal findPeriodic(Integer numberOfPayments, BigDecimal monthly, LoanAmount amount);
}
