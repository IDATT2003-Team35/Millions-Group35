package edu.ntnu.idi.idatt.millions.file;

import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockReaderTest {
  Path path = Path.of("src/test/resources/stockTests.csv");
  Path extraColumnPath = Path.of("src/test/resources/stockTestsExtraColumn.csv");

  @Test
  void stockReaderReadsCorrectAmountOfStocks() throws Exception{
    StockReader reader = new StockReader();
    assertEquals(4,reader.readStockData(path).toArray().length);
  }

  @Test
  void correctStocksInList() throws Exception {
    StockReader reader = new StockReader();
    List<Stock> stocks = reader.readStockData(path);
    assertEquals("NVDA", stocks.getFirst().getSymbol());
    assertEquals("AMZN", stocks.getLast().getSymbol());
  }

  @Test
  void lastStockHasCorrectPrice() throws Exception {
    StockReader reader = new StockReader();
    List<Stock> stocks = reader.readStockData(path);
    assertEquals(new BigDecimal("204.62"), stocks.getLast().getSalesPrice());
  }

  @Test
  void skipsLineWithFourValues() throws Exception {
    StockReader reader = new StockReader();

    List<Stock> stocks = reader.readStockData(extraColumnPath);

    assertEquals(1, stocks.size());
    assertEquals("OK", stocks.getFirst().getSymbol());
  }
}