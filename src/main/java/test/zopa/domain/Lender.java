package test.zopa.domain;

import com.google.common.base.Objects;

public final class Lender {
    private String name;

    public Lender(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lender lender = (Lender) o;
        return Objects.equal(name, lender.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
