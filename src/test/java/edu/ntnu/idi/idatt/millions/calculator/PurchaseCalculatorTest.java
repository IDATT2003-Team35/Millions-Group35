package edu.ntnu.idi.idatt.millions.calculator;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseCalculatorTest {
  private Stock testStock;
  private Share testShare;

  @BeforeEach
  void setUp() {
    testStock = new Stock("TST", "Test Inc", new BigDecimal("100.00"));
    testShare = new Share(testStock, new BigDecimal("10"), new BigDecimal("100.00"));
  }

  @Test
  void testCalculatePurchaseValues() {
    PurchaseCalculator calc = new PurchaseCalculator(testShare);

    assertEquals(new BigDecimal("1000.00"), calc.calculateGross());
    assertEquals(new BigDecimal("5.00"), calc.calculateCommission());
    assertEquals(new BigDecimal("0.00"), calc.calculateTax());
    assertEquals(new BigDecimal("1005.00"), calc.calculateTotal());
  }

  @Test
  void TestPurchaseValidators() {
    PurchaseCalculator calc = new PurchaseCalculator(testShare);
    assertNotNull(calc);

    assertThrows(NullPointerException.class,
            () -> new PurchaseCalculator(null));

    assertThrows(IllegalArgumentException.class,
            () -> new PurchaseCalculator(new Share(testStock, new BigDecimal("10"), new BigDecimal("0.00"))));

    assertThrows(IllegalArgumentException.class,
            () -> new PurchaseCalculator(new Share(testStock, new BigDecimal("0"), new BigDecimal("100.00"))));
  }
}