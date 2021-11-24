package test.zopa.domain;

import com.google.common.base.Objects;

public final class AmountAvailable {
    private Integer amount;

    public AmountAvailable(Integer amount) {
        this.amount = amount;
    }

    public Integer value() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmountAvailable that = (AmountAvailable) o;
        return Objects.equal(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}
