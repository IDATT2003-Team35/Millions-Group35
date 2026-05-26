package edu.ntnu.idi.idatt.millions.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PercentagesTest {

  @Test
  void changeReturnsExpectedPercentageValues() {
    assertEquals(
        new BigDecimal("25.0000"),
        Percentages.change(new BigDecimal("100"), new BigDecimal("125")));
    assertEquals(
        new BigDecimal("-25.0000"),
        Percentages.change(new BigDecimal("100"), new BigDecimal("75")));
  }

  @Test
  void changeInvalidInputReturnsZero() {
    assertEquals(BigDecimal.ZERO, Percentages.change(null, new BigDecimal("125")));
    assertEquals(BigDecimal.ZERO, Percentages.change(new BigDecimal("100"), null));
    assertEquals(BigDecimal.ZERO, Percentages.change(BigDecimal.ZERO, new BigDecimal("125")));
  }

  @Test
  void formatReturnsSignedRoundedPercentages() {
    assertEquals("+12.35%", Percentages.format(new BigDecimal("12.345")));
    assertEquals("-12.35%", Percentages.format(new BigDecimal("-12.345")));
    assertEquals("+0.00%", Percentages.format(BigDecimal.ZERO));
    assertEquals("", Percentages.format(null));
  }

  @Test
  void formatWithArrowReturnsDirectionalRoundedPercentages() {
    assertEquals("▲ 12.35%", Percentages.formatWithArrow(new BigDecimal("12.345")));
    assertEquals("▼ 12.35%", Percentages.formatWithArrow(new BigDecimal("-12.345")));
    assertEquals("0.00%", Percentages.formatWithArrow(BigDecimal.ZERO));
    assertEquals("", Percentages.formatWithArrow(null));
  }
}
