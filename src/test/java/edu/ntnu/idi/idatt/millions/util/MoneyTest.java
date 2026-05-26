package edu.ntnu.idi.idatt.millions.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void formatNullReturnsZeroDollars() {
    assertEquals("$0.00", Money.format(null));
  }

  @Test
  void formatRoundsAndUsesThousandsSeparator() {
    assertEquals("$1,234.57", Money.format(new BigDecimal("1234.567")));
  }

  @Test
  void formatWithArrowPositiveValueUsesUpArrow() {
    assertEquals("▲ 1,234.57", Money.formatWithArrow(new BigDecimal("1234.567")));
  }

  @Test
  void formatWithArrowNegativeValueUsesDownArrow() {
    assertEquals("▼ 1,234.57", Money.formatWithArrow(new BigDecimal("-1234.567")));
  }

  @Test
  void formatWithArrowZeroValueReturnsPlainZero() {
    assertEquals("0.00", Money.formatWithArrow(BigDecimal.ZERO));
  }

  @Test
  void formatWithArrowNullValueReturnsPlainZero() {
    assertEquals("0.00", Money.formatWithArrow(null));
  }

  @Test
  void formatWithSignPositiveValueUsesPlusPrefix() {
    assertEquals("+$1,234.57", Money.formatWithSign(new BigDecimal("1234.567")));
  }

  @Test
  void formatWithSignNegativeValueUsesMinusPrefix() {
    assertEquals("-$1,234.57", Money.formatWithSign(new BigDecimal("-1234.567")));
  }

  @Test
  void formatWithSignNullValueReturnsPositiveZeroDollars() {
    assertEquals("+$0.00", Money.formatWithSign(null));
  }
}
