package edu.ntnu.idi.idatt.millions.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

  @Test
  void formatReturnsDollarValues() {
    assertEquals("$0.00", Money.format(null));
    assertEquals("$0.00", Money.format(BigDecimal.ZERO));
    assertEquals("$1,234.57", Money.format(new BigDecimal("1234.567")));
  }

  @Test
  void formatWithArrowReturnsDirectionalValues() {
    assertEquals("▲ 1,234.57", Money.formatWithArrow(new BigDecimal("1234.567")));
    assertEquals("▼ 1,234.57", Money.formatWithArrow(new BigDecimal("-1234.567")));
    assertEquals("0.00", Money.formatWithArrow(BigDecimal.ZERO));
    assertEquals("0.00", Money.formatWithArrow(null));
  }

  @Test
  void formatWithSignReturnsSignedDollarValues() {
    assertEquals("+$1,234.57", Money.formatWithSign(new BigDecimal("1234.567")));
    assertEquals("-$1,234.57", Money.formatWithSign(new BigDecimal("-1234.567")));
    assertEquals("+$0.00", Money.formatWithSign(BigDecimal.ZERO));
    assertEquals("+$0.00", Money.formatWithSign(null));
  }
}
