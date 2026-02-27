package edu.ntnu.idi.idatt.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ShareTest {

  private Stock equinor;

  @BeforeEach
  void setUp() {
    equinor = new Stock("EQNR", "Equinor", new BigDecimal("29.20"));
  }

  @Test
  void constructorNullStockThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(null, new BigDecimal("5"), new BigDecimal("29.20")));
  }

  @Test
  void constructorNullQuantityThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(equinor, null, new BigDecimal("29.20")));
  }

  @Test
  void constructorNullPurchasePriceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(equinor, new BigDecimal("5"), null));
  }

  @Test
  void constructorZeroPurchasePriceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Share(equinor, new BigDecimal("5"), BigDecimal.ZERO));
  }

  @Test
  void constructorValidInputStoresValuesCorrectly() {
    Share share = new Share(equinor, new BigDecimal("5"), new BigDecimal("29.20"));

    assertSame(equinor, share.getStock());
    assertEquals(new BigDecimal("5"), share.getQuantity());
    assertEquals(new BigDecimal("29.20"), share.getPurchasePrice());
  }
}
