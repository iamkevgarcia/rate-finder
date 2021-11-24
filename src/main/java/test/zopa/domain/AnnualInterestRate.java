package test.zopa.domain;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public final class AnnualInterestRate {
    private BigDecimal annualRate;

    public AnnualInterestRate(@NotNull BigDecimal annualRate) {
        if (annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRateError("Interest rate must be greater than 0.");
        }

        this.annualRate = annualRate;
    }

    public BigDecimal value() {
        return annualRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnualInterestRate that = (AnnualInterestRate) o;
        return Objects.equal(annualRate, that.annualRate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(annualRate);
    }
}
