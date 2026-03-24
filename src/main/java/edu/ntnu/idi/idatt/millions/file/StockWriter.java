package edu.ntnu.idi.idatt.millions.file;

import edu.ntnu.idi.idatt.millions.model.Stock;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes stock data to a CSV file.
 */
public class StockWriter {

  /**
   * Writes the given stocks to the provided file path.
   *
   * @param filepath path to the output CSV file
   * @param stocks stocks to write
   * @throws IOException if the file cannot be written
   */
  public void writeStockData(Path filepath, List<Stock> stocks) throws IOException {

    try (BufferedWriter writer = Files.newBufferedWriter(filepath)) {

      writer.write("# Ticker,Name,Price");
      writer.newLine();
      writer.newLine();

      for (Stock stock : stocks){
        String line = stock.getSymbol() + ","
            + stock.getCompany() + ","
            + stock.getSalesPrice();
        writer.write(line);
        writer.newLine();
      }
    }
  }
}
