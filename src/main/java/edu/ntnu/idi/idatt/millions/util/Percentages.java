package edu.ntnu.idi.idatt.millions.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Percentages {
  private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

  private Percentages() {}

  public static BigDecimal change(BigDecimal from, BigDecimal to) {
    if (from == null || to == null || from.signum() == 0) {
      return BigDecimal.ZERO;
    }
    return to.subtract(from).divide(from, 4, RoundingMode.HALF_UP).multiply(HUNDRED);
  }

  public static String format(BigDecimal pct) {
    if (pct == null) return "";
    BigDecimal rounded = pct.setScale(2, RoundingMode.HALF_UP);
    String sign = rounded.signum() >= 0 ? "+" : "";
    return sign + rounded.toPlainString() + "%";
  }

  /**
   * Formats a percentage with a directional arrow instead of a sign character. Positive values get
   * ▲, negative values get ▼, zero gets no arrow.
   *
   * @param pct the percentage value
   * @return the formatted string, e.g. "▲ 12.30%" or "▼ 3.50%" or "0.00%"
   */
  public static String formatWithArrow(BigDecimal pct) {
    if (pct == null) return "";
    BigDecimal rounded = pct.setScale(2, RoundingMode.HALF_UP);
    if (rounded.signum() == 0) {
      return "0.00%";
    }
    String arrow = rounded.signum() > 0 ? "▲ " : "▼ ";
    return arrow + rounded.abs().toPlainString() + "%";
  }
}
