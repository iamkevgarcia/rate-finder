package test.zopa.ui;

import com.google.common.collect.ImmutableList;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import test.zopa.infrastructure.GuiceFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuoteFinderCLICommandTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final GuiceFactory guiceFactory = new GuiceFactory();
    private CommandLine commandLine;
    private StringWriter errorOutput;

    @BeforeEach
    private void init() {
        commandLine = new CommandLine(QuoteFinderCLICommand.class, guiceFactory);
        errorOutput = new StringWriter();
        commandLine.setErr(new PrintWriter(errorOutput));

        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void itReturnsErrorWhenMarketOffersFileWasNotProvided() {
        int exitCode = commandLine.execute();

        assertEquals(CommandLine.ExitCode.USAGE, exitCode);
        assertThat(errorOutput.toString(), containsString("Missing required parameters: <marketOffersFilePath>"));
    }

    @Test
    public void itReturnsErrorWhenLoanAmountWasNotProvided() {
        int exitCode = commandLine.execute("some_market_offers.txt");

        assertEquals(CommandLine.ExitCode.USAGE, exitCode);
        assertThat(errorOutput.toString(), containsString("Missing required parameter: <loanAmount>"));
    }

    @Test
    public void itThrowsErrorWhenMarketOffersFileWasNotFound() {
        int exitCode = commandLine.execute("non_existent.csv", "1100");

        assertEquals(CommandLine.ExitCode.SOFTWARE, exitCode);
        assertThat(errorOutput.toString(), containsString("File non_existent.csv not found."));
    }

    @Test
    public void itFindsAQuoteForGivenLoanAmount() {
        String csvWithEnoughOffers = "with_enough_offers.csv";
        List<String> validCsvMarketOffers = ImmutableList.of("csvHeader","Mark,0.075,800", "Lender,0.076,400");
        createFileWithContent(csvWithEnoughOffers, validCsvMarketOffers);

        int exitCode = commandLine.execute(csvWithEnoughOffers, "1100");

        Try.of(() -> Files.deleteIfExists(FileSystems.getDefault().getPath(csvWithEnoughOffers)));
        assertEquals(CommandLine.ExitCode.OK, exitCode);
        assertThat(outContent.toString(), containsString("Requested amount: £1100\n" +
                "Annual Interest Rate: 7.6\n" +
                "Monthly Repayment: £34.24\n" +
                "Total Repayment: £1232.72\n"));
    }

    public void createFileWithContent(String filePath, Iterable<? extends CharSequence> withContent) {
        Path invalidCsvPath = FileSystems.getDefault().getPath(filePath);
        Try.of(() -> Files.createFile(invalidCsvPath)).get();
        Try.of(() -> Files.write(invalidCsvPath, withContent, StandardCharsets.UTF_8));
    }
}
