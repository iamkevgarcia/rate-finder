package test.zopa.domain;

import com.google.common.base.Objects;
import io.vavr.Tuple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class Quote {

    private static final int TOTAL_PAYMENTS = 36;

    private final LoanAmount requestedAmount;
    private final BigDecimal annualInterestRate;
    private final BigDecimal totalRepayment;

    public Quote(LoanAmount requestedAmount, BigDecimal annualInterestRate, BigDecimal totalRepayment) {
        this.requestedAmount = requestedAmount;
        this.annualInterestRate = annualInterestRate;
        this.totalRepayment = totalRepayment;
    }

    public static Quote calculate(
            LoanRateCalculation loanRateCalculation,
            LoanAmount loanAmount,
            List<MarketOffer> offers
    ) {
        checkLoanAmountCanBeCoveredByOffers(loanAmount, offers);

        AtomicReference<Integer> amountCovered = new AtomicReference<>(0);
        Double monthlyRepayment = offers.stream()
                .map(offer -> {
                    Integer amountAvailableFromOffer = offer.amountAvailable().value();
                    Integer uncoveredAmount = (loanAmount.value() - amountCovered.get());
                    Integer amountToTakeFromOffer = amountAvailableFromOffer >= uncoveredAmount ?
                            uncoveredAmount :
                            amountAvailableFromOffer;
                    amountCovered.updateAndGet(v -> v + amountToTakeFromOffer);
                    return Tuple.of(amountToTakeFromOffer, offer.expectedAnnualRate());
                })
                .map(t -> calculateMonthlyRepayment(t._1, t._2))
                .reduce(0.0, Double::sum);

        return new Quote(
                loanAmount,
                loanRateCalculation.findAnnual(36, BigDecimal.valueOf(monthlyRepayment), loanAmount),
                BigDecimal.valueOf(monthlyRepayment * 36)
        );
    }

    private static void checkLoanAmountCanBeCoveredByOffers(LoanAmount loanAmount, List<MarketOffer> offers) {
        int totalAvailable = offers.stream()
                .mapToInt(offer -> offer.amountAvailable().value())
                .reduce(0, Integer::sum);

        if (totalAvailable < loanAmount.value()) {
            throw new NotEnoughMarketOffersError(
                    "Not enough market offers to satisfy required amount: " + loanAmount.value());
        }
    }

    private static double calculateMonthlyRepayment(Integer loanAmount, AnnualInterestRate expectedAnnualRate) {
        double i = expectedAnnualRate.value().doubleValue();
        int n = 365;
        int tp = TOTAL_PAYMENTS;

        //effective interest rate = (1 + i/n)^n - 1
        double eir = Math.pow((1 + i / n), 365) - 1;

        //monthLyRate = ((r + 1)^(1/12)) - 1
        double monthlyRate = (Math.pow(eir + 1, 1.0 / 12.0)) - 1;

        //monthlyRepayment = P * ( i(1 + i)^n/(1 + i)^n - 1 )
        return loanAmount * ((monthlyRate * (Math.pow(1 + monthlyRate, tp))) / (Math.pow(1 + monthlyRate, tp) - 1));
    }

    public LoanAmount requestedAmount() {
        return requestedAmount;
    }

    public BigDecimal annualInterestRate() {
        return annualInterestRate;
    }

    public BigDecimal totalRepayment() {
        return totalRepayment;
    }

    public BigDecimal monthlyRepayment() {
        return totalRepayment.divide(BigDecimal.valueOf(36), RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Objects.equal(requestedAmount, quote.requestedAmount) &&
                Objects.equal(annualInterestRate, quote.annualInterestRate) &&
                Objects.equal(totalRepayment, quote.totalRepayment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requestedAmount, annualInterestRate, totalRepayment);
    }
}
