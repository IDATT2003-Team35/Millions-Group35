package edu.ntnu.idi.idatt.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
}
