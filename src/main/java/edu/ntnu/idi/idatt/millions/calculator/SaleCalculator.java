package edu.ntnu.idi.idatt.millions.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Calculator for share sale transactions.
 * Calculates gross amount, commission, tax on profit, and net total.
 */
public class SaleCalculator implements TransactionCalculator {

  private final BigDecimal purchasePrice;
  private final BigDecimal salesPrice;
  private final BigDecimal quantity;
  /** Commission rate of 1% applied to gross sale amount. */
  private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.01");
  /** Tax rate of 30% applied to profit. */
  private static final BigDecimal TAX_RATE = new BigDecimal("0.3");

  /**
   * Constructs a SaleCalculator for a given share.
   * Validates that the share and its attributes are non-null and positive.
   *
   * @param share the share being sold
   * @throws NullPointerException if share or any of its attributes are null
   * @throws IllegalArgumentException if prices or quantity are not greater than zero
   */
  public SaleCalculator(Share share) {
    Objects.requireNonNull(share, "Share object cannot be null");

    BigDecimal pPrice = share.getPurchasePrice();
    BigDecimal sPrice = share.salesPrice();
    BigDecimal qty = share.getQuantity();

    Objects.requireNonNull(pPrice, "Purchase price cannot be null");
    Objects.requireNonNull(sPrice, "Sales price cannot be null");
    Objects.requireNonNull(qty, "Quantity cannot be null");

    if (pPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Purchase price must be greater than zero");
    }
    if (sPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Sales price must be greater than zero");
    }
    if (qty.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    this.purchasePrice = pPrice;
    this.salesPrice = sPrice;
    this.quantity = qty;
  }

  /**
   * Calculates the gross sale amount (sales price × quantity).
   *
   * @return the gross amount rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateGross() {
    return salesPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the commission (1% of gross amount).
   *
   * @return the commission rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates tax on profit (30% of profit if positive).
   * Profit is calculated as gross minus commission minus purchase cost.
   *
   * @return the tax amount, or zero if no profit
   */
  @Override
  public BigDecimal calculateTax() {
    BigDecimal purchaseCost = purchasePrice.multiply(quantity);
    BigDecimal profit = calculateGross()
            .subtract(calculateCommission())
            .subtract(purchaseCost);

    if (profit.compareTo(BigDecimal.ZERO) > 0) {
      return profit.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the net total (gross minus commission minus tax).
   *
   * @return the net amount rounded to 2 decimal places
   */
  @Override
  public BigDecimal calculateTotal() {
    return calculateGross()
            .subtract(calculateCommission())
            .subtract(calculateTax())
            .setScale(2, RoundingMode.HALF_UP);
  }
}