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
  void constructor_nullSymbol_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock(null, "Equinor", new BigDecimal("29.20")));
  }

  @Test
  void constructor_blankSymbol_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock(" ", "Equinor", new BigDecimal("29.20")));
  }

  @Test
  void constructor_nullCompany_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock("EQNR", null, new BigDecimal("29.20")));
  }

  @Test
  void constructor_negativeSalesPrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock("EQNR", "Equinor", new BigDecimal("-1")));
  }

  @Test
  void constructor_nullSalesPrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Stock("EQNR", "Equinor", null));
  }

  @Test
  void getSalesPrice_returnsInitialPrice() {
    assertEquals(new BigDecimal("29.20"), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPrice_validPrice_updatesCurrentPrice() {
    stock.addNewSalesPrice(new BigDecimal("35.00"));
    assertEquals(new BigDecimal("35.00"), stock.getSalesPrice());
  }

  @Test
  void addNewSalesPrice_nullPrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        stock.addNewSalesPrice(null));
  }

  @Test
  void addNewSalesPrice_negativePrice_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        stock.addNewSalesPrice(new BigDecimal("-1")));
  }
}
