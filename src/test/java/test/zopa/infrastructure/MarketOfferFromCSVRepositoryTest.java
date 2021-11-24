package test.zopa.infrastructure;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.zopa.domain.LoanAmount;
import test.zopa.domain.MarketOffer;
import test.zopa.infrastructure.persistence.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MarketOfferFromCSVRepositoryTest {

    private FileSystem fileSystem;

    @BeforeEach
    public void init() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
    }

    @Test
    public void itThrowsErrorWhenFileDoesNotExist() {
        assertThrows(CSVFileNotFound.class, () -> new MarketOfferFromCSVRepository(fileSystem, "nonExistent.txt"));
    }

    @Test
    public void itThrowsErrorWhenFileIsNotACSV() {
        String someTxtFile = "some.txt";
        createFileWithContent(someTxtFile, ImmutableList.of("hey!"));

        assertThrows(FileIsNotACSVError.class, () -> new MarketOfferFromCSVRepository(fileSystem, someTxtFile));
    }

    @Test
    public void itThrowsErrorWhenInvalidCsvWasProvided() {
        String invalidCsvFile = "invalid.csv";
        List<String> invalidCsvContent = ImmutableList.of("skipHeader","invalid, offer\nvalid,00.076,1100");
        createFileWithContent(invalidCsvFile, invalidCsvContent);

        MarketOfferFromCSVRepository repository = new MarketOfferFromCSVRepository(fileSystem, invalidCsvFile);

        UnableToReadMarketOffersFromCSV unableToReadMarketOffersFromCSV = assertThrows(
                UnableToReadMarketOffersFromCSV.class,
                () -> repository.findEnoughToSatisfyLoanAmountOrderByRate(new LoanAmount(1000))
        );
        assertThat(unableToReadMarketOffersFromCSV.getCause(), instanceOf(InvalidCSVMarketOffer.class));
    }

    @Test
    public void itFindsEnoughMarketOffersAndOrderedByRate() {
        String validCsvFile = "valid.csv";
        List<String> validCsvMarketOffers = ImmutableList.of("skip","Mark,0.075,450", "Lender,0.076,200");
        createFileWithContent(validCsvFile, validCsvMarketOffers);
        List<MarketOffer> expectedMarketOffers = ImmutableList.of(
                MarketOffer.of("Mark", "0.075", "450"),
                MarketOffer.of("Lender", "0.076", "200"));
        LoanAmount loanAmount = new LoanAmount(1100);
        MarketOfferFromCSVRepository repository = new MarketOfferFromCSVRepository(fileSystem, validCsvFile);

        List<MarketOffer> foundMarketOffers = repository.findEnoughToSatisfyLoanAmountOrderByRate(loanAmount);

        assertEquals(expectedMarketOffers, foundMarketOffers);
    }

    //TODO: Find a way to share this code with QuoteFinderCLICommandTest.
    public void createFileWithContent(String filePath, Iterable<? extends CharSequence> withContent) {
        Path csvFilePath = fileSystem.getPath(filePath);
        Try.of(() -> Files.createFile(csvFilePath)).get();
        Try.of(() -> Files.write(csvFilePath, withContent, StandardCharsets.UTF_8));
    }
}
