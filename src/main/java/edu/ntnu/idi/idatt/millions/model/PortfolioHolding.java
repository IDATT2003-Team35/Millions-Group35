package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.model.calculator.PurchaseCalculator;
import edu.ntnu.idi.idatt.millions.model.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.util.Percentages;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a grouped portfolio position for one stock.
 *
 * <p>The portfolio still stores individual purchase lots as {@link Share} objects,
 * while this class provides a read-only view of those lots grouped by stock.</p>
 */
public class PortfolioHolding {

  private final Stock stock;
  private final List<Share> shares;

  /**
   * Creates a grouped holding from shares belonging to the same stock symbol.
   *
   * @param shares the shares to group; must not be {@code null} or empty
   * @throws IllegalArgumentException if the shares are invalid or contain different symbols
   */
  public PortfolioHolding(List<Share> shares) {
    if (shares == null || shares.isEmpty()) {
      throw new IllegalArgumentException("Shares cannot be null or empty");
    }
    if (shares.stream().anyMatch(share -> share == null)) {
      throw new IllegalArgumentException("Shares cannot contain null values");
    }

    this.stock = shares.get(0).getStock();
    String symbol = stock.getSymbol();
    boolean allSharesMatchSymbol = shares.stream()
        .allMatch(share -> share.getStock().getSymbol().equals(symbol));
    if (!allSharesMatchSymbol) {
      throw new IllegalArgumentException("All shares must belong to the same stock symbol");
    }
    this.shares = new ArrayList<>(shares);
  }

  /**
   * Returns the stock represented by this holding.
   *
   * @return the stock
   */
  public Stock getStock() {
    return stock;
  }

  /**
   * Returns the stock symbol for this holding.
   *
   * @return the stock symbol
   */
  public String getSymbol() {
    return stock.getSymbol();
  }

  /**
   * Returns the company name for this holding.
   *
   * @return the company name
   */
  public String getCompany() {
    return stock.getCompany();
  }

  /**
   * Returns a copy of the purchase lots represented by this holding.
   *
   * @return a copy of the shares
   */
  public List<Share> getShares() {
    return new ArrayList<>(shares);
  }

  /**
   * Returns the total quantity owned for this stock.
   *
   * @return the total quantity
   */
  public BigDecimal getQuantity() {
    return shares.stream()
        .map(Share::getQuantity)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Returns the weighted average purchase price per share.
   *
   * @return the average purchase price rounded to two decimals
   */
  public BigDecimal getAveragePurchasePrice() {
    BigDecimal totalQuantity = getQuantity();
    if (totalQuantity.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    BigDecimal totalPurchasePrice = shares.stream()
        .map(share -> share.getPurchasePrice().multiply(share.getQuantity()))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    return totalPurchasePrice.divide(totalQuantity, 2, RoundingMode.HALF_UP);
  }

  /**
   * Returns the current price per share for this holding.
   *
   * @return the current stock price
   */
  public BigDecimal getCurrentPrice() {
    return stock.getSalesPrice();
  }

  /**
   * Returns the current gross market value before sale fees and tax.
   *
   * @return the current gross value rounded to two decimals
   */
  public BigDecimal getCurrentValue() {
    return getCurrentPrice()
        .multiply(getQuantity())
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Returns the total net gain or loss for this holding.
   *
   * @return total gain or loss after applying existing purchase and sale calculations
   */
  public BigDecimal getTotalGainLoss() {
    return getTotalSaleValue().subtract(getTotalPurchaseCost());
  }

  /**
   * Returns the total net gain or loss percentage for this holding.
   *
   * @return percentage gain or loss
   */
  public BigDecimal getTotalGainLossPercent() {
    return Percentages.change(getTotalPurchaseCost(), getTotalSaleValue());
  }

  private BigDecimal getTotalPurchaseCost() {
    return shares.stream()
        .map(share -> new PurchaseCalculator(share).calculateTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal getTotalSaleValue() {
    return shares.stream()
        .map(share -> new SaleCalculator(share).calculateTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
