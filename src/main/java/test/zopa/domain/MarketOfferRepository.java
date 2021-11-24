package test.zopa.domain;

import java.util.List;

public interface MarketOfferRepository {
    List<MarketOffer> findEnoughToSatisfyLoanAmountOrderByRate(LoanAmount loanAmount);
}
