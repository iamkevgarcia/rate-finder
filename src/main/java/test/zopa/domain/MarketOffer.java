package test.zopa.domain;

import com.google.common.base.Objects;

import java.math.BigDecimal;

public final class MarketOffer {
    private final Lender lender;
    private final AnnualInterestRate expectedAnnualRate;
    private final AmountAvailable amountAvailable;

    private MarketOffer(Lender lender, AnnualInterestRate expectedAnnualRate, AmountAvailable amountAvailable) {
        this.lender = lender;
        this.expectedAnnualRate = expectedAnnualRate;
        this.amountAvailable = amountAvailable;
    }

    public static MarketOffer of(String lender, String expectedAnnualRate, String amountAvailable) {
        return new MarketOffer(
                new Lender(lender),
                new AnnualInterestRate(new BigDecimal(expectedAnnualRate)),
                new AmountAvailable(Integer.valueOf(amountAvailable))
        );
    }

    public AnnualInterestRate expectedAnnualRate() {
        return expectedAnnualRate;
    }

    public AmountAvailable amountAvailable() {
        return amountAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketOffer that = (MarketOffer) o;
        return Objects.equal(lender, that.lender) &&
                Objects.equal(expectedAnnualRate, that.expectedAnnualRate) &&
                Objects.equal(amountAvailable, that.amountAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lender, expectedAnnualRate, amountAvailable);
    }
}
