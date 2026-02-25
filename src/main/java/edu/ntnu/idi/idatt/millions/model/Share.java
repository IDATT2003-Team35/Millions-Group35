package edu.ntnu.idi.idatt.millions.model;

import java.math.BigDecimal;

/**
 * Represents an owned share of a specific {@link Stock} with a quantity and the
 * purchase price per share.
 *
 * <p>Instances are immutable after construction.</p>
 */
public class Share {

  /**
   * The stock represented by this share.
   */
  private final Stock stock;

  /**
   * The quantity of shares owned.
   */
  private final BigDecimal quantity;

  /**
   * The purchase price per share.
   */
  private final BigDecimal purchasePrice;

  /**
   * Constructs a new {@code Share}.
   *
   * @param stock the stock for this share; must not be {@code null}
   * @param quantity the quantity owned; must not be {@code null}
   * @param purchasePrice the purchase price per share; must not be {@code null} and,
   *                     not negative
   * @throws IllegalArgumentException if {@code stock} is {@code null}
   * @throws IllegalArgumentException if {@code quantity} is {@code null}
   * @throws IllegalArgumentException if {@code purchasePrice} is {@code null} or
   *                                  {@code purchasePrice.compareTo(BigDecimal.ZERO) >= 0}
   */
  public Share(Stock stock, BigDecimal quantity, BigDecimal purchasePrice){
    if(stock == null){
      throw new IllegalArgumentException("Stock cant be null");
    }
    if(quantity == null){
      throw new IllegalArgumentException("Quantity cant be null");
    }
    if(purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0){
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
  public BigDecimal getPurchasePrice(){
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
}
