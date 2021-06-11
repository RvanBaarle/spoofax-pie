package mb.statix.completions.bench.performance;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a CSV file.
 */
public final class CsvFile implements Closeable {

    private final CSVPrinter printer;

    /**
     * Creates a CSV file that writes to the given buffer.
     *
     * Close the returned object.
     *
     * @param appendable the buffer to write to
     * @return the CSV file
     */
    public static CsvFile create(Appendable appendable) throws IOException {
        final CSVFormat format = CSVFormat.EXCEL;
        appendable.append("sep=").append(String.valueOf(format.getDelimiter())).append("\n");
        return new CsvFile(format
            .withHeader(BenchmarkStats.CsvRow.getCsvHeaders())
            .print(appendable));
    }

    /**
     * Creates a CSV file that writes to the given path.
     *
     * Close the returned object.
     *
     * @param path the path to write to
     * @return the CSV file
     */
    public static CsvFile create(Path path) throws IOException {
        return create(Files.newBufferedWriter(path, StandardCharsets.UTF_8));
    }

    /**
     * Initializes a new instance of the {@link CsvFile} class.
     *
     * @param printer the CSV printer to use
     */
    private CsvFile(CSVPrinter printer) {
        this.printer = printer;
    }

    /**
     * Adds a row to the CSV.
     *
     * @param row the row to add
     */
    public void addRow(BenchmarkStats.CsvRow row) {
        try {
            row.printToCsv(this.printer);
        } catch(IOException ex) {
            throw new RuntimeException("Unable to append CSV.", ex);
        }
    }

    @Override
    public void close() throws IOException {
        printer.close(true);
    }
}
