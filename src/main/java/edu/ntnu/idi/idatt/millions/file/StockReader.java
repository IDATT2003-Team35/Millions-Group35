package edu.ntnu.idi.idatt.millions.file;

import edu.ntnu.idi.idatt.millions.model.Stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads stock data from a CSV file into Stock objects.
 */
public class StockReader {

  /**
   * Reads stock data from the given file path.
   *
   * @param filepath path to the CSV file
   * @return list of parsed Stock entries
   * @throws IOException if the file cannot be read
   */
  public List<Stock> readStockData(Path filepath) throws IOException {
    List<Stock> stocks= new ArrayList<>();

    try (BufferedReader reader = Files.newBufferedReader(filepath)) {
      String line;

      while ((line = reader.readLine()) != null) {
        var values = line.split(",");
        if(!line.isBlank() && !line.startsWith("#") && values.length == 3){
          stocks.add(new Stock(values[0], values[1], new BigDecimal(values[2])));
        }
      }
    }
    return stocks;
  }
}
