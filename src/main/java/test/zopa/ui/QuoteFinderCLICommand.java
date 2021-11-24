package test.zopa.ui;

import io.vavr.control.Try;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import test.zopa.application.FindLowestQuoteUseCase;
import test.zopa.domain.Quote;
import test.zopa.infrastructure.ExcelLoanRateCalculation;
import test.zopa.infrastructure.GuiceFactory;
import test.zopa.infrastructure.persistence.MarketOfferFromCSVRepository;

import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.util.concurrent.Callable;

@Command(name = "quote-finder", mixinStandardHelpOptions = true, version = "1.0")
public final class QuoteFinderCLICommand implements Callable<Integer> {

    @Parameters(index = "0")
    protected String marketOffersFilePath;
    @Parameters(index = "1")
    protected Integer loanAmount;

    public static void main(String... args) {
        int exitCode = new CommandLine(QuoteFinderCLICommand.class, new GuiceFactory()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        MarketOfferFromCSVRepository repository = new MarketOfferFromCSVRepository(
                FileSystems.getDefault(),
                marketOffersFilePath
        );
        FindLowestQuoteUseCase findLowestQuoteUseCase = new FindLowestQuoteUseCase(
                repository,
                new ExcelLoanRateCalculation()
        );

        Try<Quote> foundQuote = Try.of(() -> findLowestQuoteUseCase.find(loanAmount));

        if (foundQuote.isFailure()) {
            System.err.println("Error: " + foundQuote.getCause().getMessage());
            return CommandLine.ExitCode.SOFTWARE;
        }

        System.out.println("Requested amount: £" + loanAmount);
        System.out.printf("Annual Interest Rate: %.1f\n", foundQuote.get().annualInterestRate().multiply(BigDecimal.valueOf(100)));
        System.out.printf("Monthly Repayment: £%.2f\n", foundQuote.get().monthlyRepayment());
        System.out.printf("Total Repayment: £%.2f\n", foundQuote.get().totalRepayment());

        return CommandLine.ExitCode.OK;
    }
}