package test.zopa.domain;

import com.google.common.base.Objects;

public final class LoanAmount {
    private final Integer amount;

    public LoanAmount(Integer amount) {
        checkAmountIsInValidRange(amount);
        checkAmountIsAnIncrementOf100(amount);

        this.amount = amount;
    }

    private void checkAmountIsAnIncrementOf100(Integer amount) {
        if (amount % 100 != 0) {
            throw new InvalidLoanAmountError("Amount can only be increment of 100.");
        }
    }

    private void checkAmountIsInValidRange(Integer amount) {
        if (amount < 1000 || amount > 15000) {
            throw new InvalidLoanAmountError("Loan amount can only be from 1000 to 15000 inclusive.");
        }
    }

    public Integer value() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanAmount that = (LoanAmount) o;
        return Objects.equal(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}
