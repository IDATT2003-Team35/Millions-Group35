package edu.ntnu.idi.idatt.millions.file;

import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
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
  void lineWithFourValuesThrowsException() {
    StockReader reader = new StockReader();

    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reader.readStockData(extraColumnPath)
    );

    assertTrue(exception.getMessage().contains("line 2"));
  }

  @Test
  void nullFilePathThrowsException() {
    StockReader reader = new StockReader();

    assertThrows(IllegalArgumentException.class,
            () -> reader.readStockData((Path) null));
  }

  @Test
  void valuesAreTrimmedBeforeStockIsCreated() throws Exception {
    List<Stock> stocks = readFromText("""
       AAPL , Apple Inc. , 276.43
      """);

    assertEquals("AAPL", stocks.getFirst().getSymbol());
    assertEquals("Apple Inc.", stocks.getFirst().getCompany());
    assertEquals(new BigDecimal("276.43"), stocks.getFirst().getSalesPrice());
  }

  @Test
  void invalidPriceThrowsExceptionWithLineNumber() {
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> readFromText("""
          AAPL,Apple Inc.,abc
          """)
    );

    assertTrue(exception.getMessage().contains("line 1"));
  }

  @Test
  void blankAndCommentLinesAreIgnored() throws Exception {
    List<Stock> stocks = readFromText("""
         # Ticker,Name,Price

      AAPL,Apple Inc.,276.43
      """);

    assertEquals(1, stocks.size());
    assertEquals("AAPL", stocks.getFirst().getSymbol());
  }

  private List<Stock> readFromText(String text) throws Exception {
    StockReader reader = new StockReader();
    return reader.readStockData(new BufferedReader(new StringReader(text)));
  }
}