package edu.ntnu.idi.idatt.millions.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/** Helpers for formatting monetary values for display. */
public final class Money {

  private Money() {}

  /**
   * Formats a money value as a dollar string with thousand separators and exactly two decimal
   * places. Returns "$0.00" for null input.
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

  /**
   * Formats a money change as an arrow followed by a thousand-separated number. Positive values get
   * ▲, negative values get ▼, zero gets a plain "0.00".
   *
   * @param value the change to format
   * @return the formatted string, e.g. "▲ 1,234.56" or "▼ 100.80" or "0.00"
   */
  public static String formatWithArrow(BigDecimal value) {
    if (value == null) {
      return "0.00";
    }
    BigDecimal rounded = value.setScale(2, RoundingMode.HALF_UP);
    if (rounded.signum() == 0) {
      return "0.00";
    }
    String arrow = rounded.signum() > 0 ? "▲ " : "▼ ";
    return arrow + String.format(Locale.US, "%,.2f", rounded.abs());
  }

  /**
   * Formats a money change with an explicit sign prefix and dollar sign. Positive and zero values
   * get "+$...", negative values get "-$...".
   *
   * @param value the change to format
   * @return the formatted string, e.g. "+$1,234.56" or "-$909.60"
   */
  public static String formatWithSign(BigDecimal value) {
    if (value == null) {
      return "+$0.00";
    }
    String sign = value.signum() >= 0 ? "+" : "-";
    return sign + format(value.abs());
  }
}
