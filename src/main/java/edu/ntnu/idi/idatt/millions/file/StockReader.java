package edu.ntnu.idi.idatt.millions.file;

import edu.ntnu.idi.idatt.millions.model.Stock;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Reads stock data from a CSV file into Stock objects. */
public class StockReader {

  /** Creates a stock reader. */
  public StockReader() {}

  /**
   * Reads stock data from the given file path.
   *
   * @param filepath path to the CSV file
   * @return list of parsed Stock entries
   * @throws IOException if the file cannot be read
   * @throws StockParseException if the file contains invalid stock data
   */
  public List<Stock> readStockData(Path filepath) throws IOException, StockParseException {
    if (filepath == null) {
      throw new IllegalArgumentException("filepath cannot be null");
    }

    try (BufferedReader reader = Files.newBufferedReader(filepath)) {
      return readStockData(reader);
    }
  }

  /**
   * Reads stock data from an already opened reader.
   *
   * <p>This overload is package-private so tests can verify parser behavior without creating
   * temporary files.
   *
   * @param reader reader containing CSV stock data
   * @return list of parsed Stock entries
   * @throws IOException if the reader cannot be read
   * @throws StockParseException if the reader contains invalid stock data
   */
  List<Stock> readStockData(BufferedReader reader) throws IOException, StockParseException {
    List<Stock> stocks = new ArrayList<>();
    String line;
    int lineNumber = 0;

    while ((line = reader.readLine()) != null) {
      lineNumber++;
      parseLine(line, lineNumber).ifPresent(stocks::add);
    }
    return stocks;
  }

  private Optional<Stock> parseLine(String line, int lineNumber) throws StockParseException {
    String trimmedLine = line.trim();
    String[] values = line.split(",", -1);

    if (trimmedLine.isBlank() || trimmedLine.startsWith("#")) {
      return Optional.empty();
    }

    if (values.length != 3) {
      throw new StockParseException(
          "Invalid stock data on line "
              + lineNumber
              + ": expected 3 values but found "
              + values.length);
    }

    String symbol = values[0].trim();
    String company = values[1].trim();
    String stringPrice = values[2].trim();
    BigDecimal price = parsePrice(stringPrice, lineNumber);

    try {
      return Optional.of(new Stock(symbol, company, price));
    } catch (IllegalArgumentException e) {
      throw new StockParseException("Invalid stock data on line " + lineNumber, e);
    }
  }

  private BigDecimal parsePrice(String stringPrice, int lineNumber) throws StockParseException {
    try {
      return new BigDecimal(stringPrice);
    } catch (NumberFormatException e) {
      throw new StockParseException(
          "Invalid stock price on line " + lineNumber + ": " + stringPrice, e);
    }
  }
}
