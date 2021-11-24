package test.zopa.infrastructure;

import test.zopa.domain.LoanAmount;
import test.zopa.domain.LoanRateCalculation;

import java.math.BigDecimal;

public final class ExcelLoanRateCalculation implements LoanRateCalculation {

    @Override
    public BigDecimal findAnnual(Integer numberOfPayments, BigDecimal monthly, LoanAmount amount) {
        return findPeriodic(numberOfPayments, monthly, amount).multiply(BigDecimal.valueOf(12));
    }

    @Override
    public BigDecimal findPeriodic(Integer numberOfPayments, BigDecimal monthly, LoanAmount amount) {
        return BigDecimal.valueOf(calculateRate(numberOfPayments.doubleValue(), monthly.negate().doubleValue(), amount.value().doubleValue()));
    }

    //Simplified Excel formula used for rate calculation.
    private double calculateRate(double nper, double pmt, double pv) {
        //FROM MS http://office.microsoft.com/en-us/excel-help/rate-HP005209232.aspx
        int FINANCIAL_MAX_ITERATIONS = 20;//Bet accuracy with 128
        double FINANCIAL_PRECISION = 0.0000001;//1.0e-8

        double y, y0, y1, x0, x1 = 0, f = 0, i = 0, fv = 0, type = 0;
        double rate = 0.1;
        f = Math.exp(nper * Math.log(1 + rate));
        y = pv * f + pmt * (1 / rate + type) * (f - 1) + fv;
        y0 = pv + pmt * nper + fv;
        y1 = pv * f + pmt * (1 / rate + type) * (f - 1) + fv;

        // find root by Newton secant method
        i = x0 = 0.0;
        x1 = rate;
        while ((Math.abs(y0 - y1) > FINANCIAL_PRECISION) && (i < FINANCIAL_MAX_ITERATIONS)) {
            rate = (y1 * x0 - y0 * x1) / (y1 - y0);
            x0 = x1;
            x1 = rate;

            if (Math.abs(rate) < FINANCIAL_PRECISION) {
                y = pv * (1 + nper * rate) + pmt * (1 + rate * type) * nper + fv;
            } else {
                f = Math.exp(nper * Math.log(1 + rate));
                y = pv * f + pmt * (1 / rate + type) * (f - 1) + fv;
            }

            y0 = y1;
            y1 = y;
            ++i;
        }
        return rate;
    }
}
