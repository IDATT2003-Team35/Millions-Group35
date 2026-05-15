package edu.ntnu.idi.idatt.millions.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Helpers for formatting monetary values for display.
 */
public final class Money {

  private Money() {}

  /**
   * Formats a money value as a dollar string with thousand separators and
   * exactly two decimal places. Returns "$0.00" for null input.
   *
   * @param value the amount to format
   * @return the formatted string, e.g. "$10,000.00"
   */
  public static String format(BigDecimal value) {
    if (value == null) {
      return "$0.00";
    }
    return String.format(Locale.US, "$%,.2f", value.setScale(2, RoundingMode.HALF_UP));
  }
}
