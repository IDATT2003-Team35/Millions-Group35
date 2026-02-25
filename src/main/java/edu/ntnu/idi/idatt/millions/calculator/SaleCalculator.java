package edu.ntnu.idi.idatt.millions.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
   *
   * @param share the share being sold
   */
  public SaleCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.salesPrice = share.salesPrice();
    this.quantity = share.getQuantity();
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
