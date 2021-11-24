package test.zopa.application;

import org.jetbrains.annotations.NotNull;
import test.zopa.domain.*;

import java.util.List;

public final class FindLowestQuoteUseCase {

    private final MarketOfferRepository marketOfferRepository;
    private final LoanRateCalculation loanRateCalculation;

    public FindLowestQuoteUseCase(MarketOfferRepository marketOfferRepository, LoanRateCalculation loanRateCalculation) {
        this.marketOfferRepository = marketOfferRepository;
        this.loanRateCalculation = loanRateCalculation;
    }

    @NotNull
    public Quote find(@NotNull Integer loanAmountRaw) {
        LoanAmount loanAmount = new LoanAmount(loanAmountRaw);

        List<MarketOffer> foundOffers = marketOfferRepository.findEnoughToSatisfyLoanAmountOrderByRate(loanAmount);

        return Quote.calculate(loanRateCalculation, loanAmount, foundOffers);
    }
}
