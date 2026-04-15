package edu.ntnu.idi.idatt.millions.model.calculator;

import edu.ntnu.idi.idatt.millions.model.Share;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Calculator for share purchase transactions.
 * Calculates gross amount, commission, and total cost.
 */
public class PurchaseCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;
  /** Commission rate of 0.5% applied to gross purchase amount. */
  private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.005");

  /**
   * Constructs a PurchaseCalculator for a given share.
   * Validates that the share and its attributes are non-null and positive.
   *
   * @param share the share being purchased
   * @throws NullPointerException if share or its attributes are null
   * @throws IllegalArgumentException if purchase price or quantity is not greater than zero
   */
  public PurchaseCalculator(Share share) {
    Objects.requireNonNull(share, "Share object cannot be null");

    BigDecimal price = share.getPurchasePrice();
    BigDecimal qty = share.getQuantity();

    Objects.requireNonNull(price, "Purchase price cannot be null");
    Objects.requireNonNull(qty, "Quantity cannot be null");

    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Purchase price must be greater than zero");
    }
    if (qty.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    this.purchasePrice = price;
    this.quantity = qty;
  }

  /**
   * Calculates the gross purchase amount (purchase price × quantity).
   *
   * @return the gross amount rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateGross() {
    return purchasePrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the commission (0.5% of gross amount).
   *
   * @return the commission rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates tax for purchase transactions.
   * Purchases are not subject to tax.
   *
   * @return zero with 2 decimal places
   */
  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the total cost (gross plus commission).
   *
   * @return the total amount rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross()
            .add(calculateCommission())
            .setScale(2, RoundingMode.HALF_UP);
  }
}
