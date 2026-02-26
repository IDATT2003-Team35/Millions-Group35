package edu.ntnu.idi.idatt.millions.calculator;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SaleCalculatorTest {
  private Stock testStock;
  private Share breakEvenShare;
  private Share profitShare;
  private Share lossShare;

  @BeforeEach
  void setUp() {
    testStock = new Stock("TST", "Test Inc", new BigDecimal("100.00"));
    breakEvenShare = new Share(testStock, new BigDecimal("10"), new BigDecimal("100.00"));
    profitShare = new Share(testStock, new BigDecimal("10"), new BigDecimal("50.00"));
    lossShare = new Share(testStock, new BigDecimal("10"), new BigDecimal("200.00"));
  }

  @Test
  void testCalculateSaleValuesBreakEven() {
    SaleCalculator calc = new SaleCalculator(breakEvenShare);

    // gross = 100 * 10 = 1000
    assertEquals(new BigDecimal("1000.00"), calc.calculateGross());
    // commission = gross / 10 = 10
    assertEquals(new BigDecimal("10.00"), calc.calculateCommission());
    // tax = gross - commission - 100 * 10 = 0
    assertEquals(new BigDecimal("0.00"), calc.calculateTax());
    // total = gross - commission - tax = 990
    assertEquals(new BigDecimal("990.00"), calc.calculateTotal());
  }
  @Test
  void testCalculateSaleValuesProfit() {
    SaleCalculator calc = new SaleCalculator(profitShare);

    assertEquals(new BigDecimal("1000.00"), calc.calculateGross());
    assertEquals(new BigDecimal("10.00"), calc.calculateCommission());
    assertEquals(new BigDecimal("147.00"), calc.calculateTax());
    assertEquals(new BigDecimal("843.00"), calc.calculateTotal());
  }
  @Test
  void testCalculateSaleValuesLoss() {
    SaleCalculator calc = new SaleCalculator(lossShare);

    assertEquals(new BigDecimal("1000.00"), calc.calculateGross());
    assertEquals(new BigDecimal("10.00"), calc.calculateCommission());
    // negativ gross gives 0 tax
    assertEquals(new BigDecimal("0.00"), calc.calculateTax());
    assertEquals(new BigDecimal("990.00"), calc.calculateTotal());
  }

  @Test
  void TestSaleValidators() {
    SaleCalculator calc = new SaleCalculator(profitShare);
    assertNotNull(calc);

    assertThrows(NullPointerException.class,
            () -> new SaleCalculator(null));

    assertThrows(IllegalArgumentException.class,
            () -> new SaleCalculator(new Share(testStock, new BigDecimal("0"), new BigDecimal("100.00"))));

    assertThrows(IllegalArgumentException.class,
            () -> new SaleCalculator(new Share(testStock, new BigDecimal("10"), new BigDecimal("0.00"))));
  }
}