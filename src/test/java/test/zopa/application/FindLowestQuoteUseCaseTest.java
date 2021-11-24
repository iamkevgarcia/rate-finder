package test.zopa.application;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.zopa.domain.*;
import test.zopa.infrastructure.ExcelLoanRateCalculation;
import test.zopa.infrastructure.persistence.UnableToReadMarketOffersFromCSV;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FindLowestQuoteUseCaseTest {

    private MarketOfferRepository marketOfferRepo;
    private FindLowestQuoteUseCase useCase;

    @BeforeEach
    public void init() {
        marketOfferRepo = mock(MarketOfferRepository.class);
        useCase = new FindLowestQuoteUseCase(marketOfferRepo, new ExcelLoanRateCalculation());
    }

    @Test
    public void itThrowsErrorWhenThereWereNotEnoughMarketOffersForGivenLoanAmount() {
        LoanAmount loanAmount = new LoanAmount(2100);
        List<MarketOffer> fewOffers = ImmutableList.of(MarketOffer.of("Mark", "0.090", "500"));
        when(marketOfferRepo.findEnoughToSatisfyLoanAmountOrderByRate(loanAmount)).thenReturn(fewOffers);

        assertThrows(NotEnoughMarketOffersError.class, () -> useCase.find(loanAmount.value()));
    }

    @Test
    public void itFindsTheLowestQuotePossibleFromAvailableMarketOffers() {
        LoanAmount loanAmount = new LoanAmount(1100);
        List<MarketOffer> marketOffers = ImmutableList.of(
                MarketOffer.of("Mark", "0.090", "500"),
                MarketOffer.of("Lender", "0.085", "500"),
                MarketOffer.of("Oliver", "0.070", "500"),
                MarketOffer.of("Zopa", "0.070", "500"));
        Quote expectedQuote = new Quote(
                loanAmount,
                new BigDecimal("0.086222278259818980"),
                new BigDecimal("1252.31849493777")
        );
        when(marketOfferRepo.findEnoughToSatisfyLoanAmountOrderByRate(loanAmount)).thenReturn(marketOffers);

        Quote foundQuote = useCase.find(loanAmount.value());

        assertEquals(expectedQuote, foundQuote);
    }
}
