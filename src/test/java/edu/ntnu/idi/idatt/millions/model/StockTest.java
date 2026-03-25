package edu.ntnu.idi.idatt.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockTest {

  private Stock stock;

  @BeforeEach
  void setUp() {
    stock = new Stock("EQNR", "Equinor", new BigDecimal("29.20"));
  }

  @Test
  void constructorNullSymbolThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock(null, "Equinor", new BigDecimal("29.20")));
  }

  @Test
  void constructorBlankSymbolThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock(" ", "Equinor", new BigDecimal("29.20")));
  }

  @Test
  void constructorNullCompanyThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock("EQNR", null, new BigDecimal("29.20")));
  }

  @Test
  void constructorNegativeSalesPriceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock("EQNR", "Equinor", new BigDecimal("-1")));
  }

  @Test
  void constructorNullSalesPriceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock("EQNR", "Equinor", null));
  }

  @Test
  void getSalesPriceReturnsInitialPrice() {
    assertEquals(new BigDecimal("29.20"), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPriceValidPriceUpdatesCurrentPrice() {
    stock.addNewSalesPrice(new BigDecimal("35.00"));
    assertEquals(new BigDecimal("35.00"), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPriceNullPriceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        stock.addNewSalesPrice(null));
  }

  @Test
  void addNewSalesPriceNegativePriceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        stock.addNewSalesPrice(new BigDecimal("-1")));
  }

  @Test
  void getHistoricalPricesReturnsAllRecordedPrices() {
    stock.addNewSalesPrice(new BigDecimal("30.00"));
    stock.addNewSalesPrice(new BigDecimal("31.50"));

    List<BigDecimal> history = stock.getHistoricalPrices();

    assertEquals(3, history.size());
    assertEquals(new BigDecimal("29.20"), history.get(0));
    assertEquals(new BigDecimal("31.50"), history.get(2));
  }

  @Test
  void getHighestPriceReturnsCorrectMaximumValue() {
    stock.addNewSalesPrice(new BigDecimal("40.00"));
    stock.addNewSalesPrice(new BigDecimal("25.00"));
    stock.addNewSalesPrice(new BigDecimal("35.00"));

    assertEquals(new BigDecimal("40.00"), stock.getHighestPrice());
  }

  @Test
  void getLowestPriceReturnsCorrectMinimumValue() {
    stock.addNewSalesPrice(new BigDecimal("40.00"));
    stock.addNewSalesPrice(new BigDecimal("25.00"));
    stock.addNewSalesPrice(new BigDecimal("35.00"));

    assertEquals(new BigDecimal("25.00"), stock.getLowestPrice());
  }

  @Test
  void getLatestPriceChangeReturnsCorrectDifference() {
    stock.addNewSalesPrice(new BigDecimal("35.20")); // 35.20 - 29.20 = 6.00

    assertEquals(new BigDecimal("6.00"), stock.getLatestPriceChange());
  }

  @Test
  void getLatestPriceChangeReturnsZeroWhenOnlyOnePriceExists() {
    BigDecimal expected = BigDecimal.ZERO;
    assertEquals(expected, stock.getLatestPriceChange());
  }
}
