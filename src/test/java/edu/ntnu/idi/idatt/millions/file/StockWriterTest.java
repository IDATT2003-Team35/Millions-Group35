package edu.ntnu.idi.idatt.millions.file;

import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockWriterTest {

  @TempDir
  Path tempDir;

  @Test
  void writesCorrectNumberOfLines() throws Exception {
    Path outputFile = tempDir.resolve("output.csv");
    StockWriter writer = new StockWriter();
    List<Stock> stocks = List.of(
        new Stock("AAPL", "Apple.", new BigDecimal("276.43")),
        new Stock("MSFT", "Microsoft", new BigDecimal("404.68"))
    );

    writer.writeStockData(outputFile, stocks);

    List<String> lines = Files.readAllLines(outputFile);
    assertEquals(4, lines.size());
  }

  @Test
  void writtenFileContainsCommentHeader() throws Exception {
    Path outputFile = tempDir.resolve("output.csv");
    StockWriter writer = new StockWriter();
    List<Stock> stocks = List.of(
        new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43"))
    );

    writer.writeStockData(outputFile, stocks);

    List<String> lines = Files.readAllLines(outputFile);
    assertTrue(lines.getFirst().startsWith("#"));
  }

  @Test
  void writtenFileCanBeReadBackCorrectly() throws Exception {
    Path outputFile = tempDir.resolve("output.csv");
    StockWriter writer = new StockWriter();
    StockReader reader = new StockReader();
    List<Stock> originalStocks = List.of(
        new Stock("AAPL", "Apple Inc.", new BigDecimal("276.43")),
        new Stock("NVDA", "Nvidia", new BigDecimal("191.27"))
    );

    writer.writeStockData(outputFile, originalStocks);
    List<Stock> readStocks = reader.readStockData(outputFile);

    assertEquals(2, readStocks.size());
    assertEquals("AAPL", readStocks.getFirst().getSymbol());
    assertEquals("Apple Inc.", readStocks.getFirst().getCompany());
    assertEquals(new BigDecimal("276.43"), readStocks.getFirst().getSalesPrice());
    assertEquals("NVDA", readStocks.getLast().getSymbol());
  }

  @Test
  void writesEmptyListWithoutStockLines() throws Exception {
    Path outputFile = tempDir.resolve("output.csv");
    StockWriter writer = new StockWriter();

    writer.writeStockData(outputFile, List.of());

    List<String> lines = Files.readAllLines(outputFile);
    assertEquals(2, lines.size());
    assertTrue(lines.getFirst().startsWith("#"));
  }
}
