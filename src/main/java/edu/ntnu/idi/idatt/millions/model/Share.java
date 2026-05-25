package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.model.calculator.PurchaseCalculator;
import edu.ntnu.idi.idatt.millions.model.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import java.math.BigDecimal;

/**
 * Represents an owned share of a specific {@link Stock} with a quantity and the purchase price per
 * share.
 *
 * <p>Instances are immutable after construction.
 */
public class Share {

  private final Stock stock;

  private final BigDecimal quantity;

  private final BigDecimal purchasePrice;

  /**
   * Constructs a new {@code Share}.
   *
   * @param stock the stock for this share; must not be {@code null}
   * @param quantity the quantity owned; must not be {@code null}
   * @param purchasePrice the purchase price per share; must not be {@code null} and, not negative
   * @throws IllegalArgumentException if {@code stock} is {@code null}
   * @throws IllegalArgumentException if {@code quantity} is {@code null}
   * @throws IllegalArgumentException if {@code purchasePrice} is {@code null} or {@code
   *     purchasePrice.compareTo(BigDecimal.ZERO) >= 0}
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice) {
    if (stock == null) {
      throw new IllegalArgumentException("Stock cant be null");
    }
    if (quantity == null) {
      throw new IllegalArgumentException("Quantity cant be null");
    }
    if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Purchase price cant be null");
    }
    this.stock = stock;
    this.purchasePrice = purchasePrice;
    this.quantity = quantity;
  }

  /**
   * Returns the stock for this share.
   *
   * @return the {@link Stock} instance
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Returns the purchase price per share.
   *
   * @return the purchase price as a {@link BigDecimal}
   */
  public BigDecimal getPurchasePrice() {
    return purchasePrice;
  }

  /**
   * Returns the quantity of shares owned.
   *
   * @return the quantity as a {@link BigDecimal}
   */
  public BigDecimal getQuantity() {
    return quantity;
  }

  /**
   * Returns the net gain or loss in dollars for this share at the current market price, accounting
   * for commission on both the original purchase and a hypothetical sale, and the tax that would be
   * paid on the realized gain.
   *
   * @return the net gain (positive) or loss (negative) as a {@link BigDecimal}
   */
  public BigDecimal getNetGainLoss() {
    BigDecimal saleTotal = new SaleCalculator(this).calculateTotal();
    BigDecimal purchaseTotal = new PurchaseCalculator(this).calculateTotal();
    return saleTotal.subtract(purchaseTotal);
  }

  /**
   * Returns the net gain or loss as a percentage of the total purchase cost, accounting for
   * commissions and tax.
   *
   * @return the net gain/loss percentage as a {@link BigDecimal}
   */
  public BigDecimal getNetGainLossPercent() {
    BigDecimal saleTotal = new SaleCalculator(this).calculateTotal();
    BigDecimal purchaseTotal = new PurchaseCalculator(this).calculateTotal();
    return Percentages.change(purchaseTotal, saleTotal);
  }
}
