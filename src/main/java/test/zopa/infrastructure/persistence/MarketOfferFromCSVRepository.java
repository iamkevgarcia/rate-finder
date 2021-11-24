package test.zopa.infrastructure.persistence;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import test.zopa.domain.LoanAmount;
import test.zopa.domain.MarketOffer;
import test.zopa.domain.MarketOfferRepository;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MarketOfferFromCSVRepository implements MarketOfferRepository {

    private final FileSystem fileSystem;
    private final String csvFilePath;

    public MarketOfferFromCSVRepository(@NotNull FileSystem fileSystem, @NotNull String csvFilePath) {
        if (!Files.exists(fileSystem.getPath(csvFilePath))) {
            throw new CSVFileNotFound("File " + csvFilePath + " not found.");
        }

        guardFileExtension(csvFilePath);

        this.fileSystem = fileSystem;
        this.csvFilePath = csvFilePath;
    }

    private void guardFileExtension(String csvFilePath) {
        if (!csvFilePath.endsWith(".csv")) {
            throw new FileIsNotACSVError("Provided file: " + csvFilePath + " is not a csv.");
        }
    }

    @Override
    public List<MarketOffer> findEnoughToSatisfyLoanAmountOrderByRate(@NotNull LoanAmount loanAmount) {
        Function<String, MarketOffer> mapToItem = (row) -> {
            String[] marketOfferCsvRow = row.split(",");
            guardMarketOfferCsvRow(marketOfferCsvRow);

            return MarketOffer.of(marketOfferCsvRow[0], marketOfferCsvRow[1], marketOfferCsvRow[2]);
        };

        return Try.withResources(() -> Files.newBufferedReader(fileSystem.getPath(csvFilePath)))
                .of(r -> r.lines().skip(1)
                        .map(mapToItem)
                        .sorted(Comparator.comparing(s -> s.expectedAnnualRate().value()))
                        .collect(Collectors.toList()))
                .getOrElseThrow(t -> new UnableToReadMarketOffersFromCSV(t.getMessage(), t));
    }

    private void guardMarketOfferCsvRow(String[] marketOfferCsvRow) {
        if (marketOfferCsvRow.length < 3) {
            throw new InvalidCSVMarketOffer("Invalid provided csv market offer.");
        }
    }
}
