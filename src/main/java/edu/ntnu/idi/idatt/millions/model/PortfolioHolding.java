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
 * <p>The portfolio still stores individual purchase lots as {@link Share} objects, while this class
 * provides a read-only view of those lots grouped by stock.
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
    boolean allSharesMatchSymbol =
        shares.stream().allMatch(share -> share.getStock().getSymbol().equals(symbol));
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
    return shares.stream().map(Share::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
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

    BigDecimal totalPurchasePrice =
        shares.stream()
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
    return getCurrentPrice().multiply(getQuantity()).setScale(2, RoundingMode.HALF_UP);
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

  /**
   * Estimates the net sale value for selling a quantity from this holding.
   *
   * <p>The estimate uses the same purchase-lot order as an actual quantity sale, but it does not
   * mutate the portfolio.
   *
   * @param quantity quantity to sell; must be greater than zero and no larger than the owned
   *     quantity
   * @return estimated sale value after commission and tax
   * @throws IllegalArgumentException if quantity is null, not positive, or greater than the owned
   *     quantity
   */
  public BigDecimal getEstimatedSaleValue(BigDecimal quantity) {
    return createPreviewShares(quantity).stream()
        .map(share -> new SaleCalculator(share).calculateTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Estimates the net gain or loss for selling a quantity from this holding.
   *
   * <p>The estimate compares the net sale value with the purchase cost of the purchase lots that
   * would be sold by an actual quantity sale.
   *
   * @param quantity quantity to sell; must be greater than zero and no larger than the owned
   *     quantity
   * @return estimated net gain or loss
   * @throws IllegalArgumentException if quantity is null, not positive, or greater than the owned
   *     quantity
   */
  public BigDecimal getEstimatedGainLoss(BigDecimal quantity) {
    List<Share> previewShares = createPreviewShares(quantity);
    BigDecimal saleValue =
        previewShares.stream()
            .map(share -> new SaleCalculator(share).calculateTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal purchaseCost =
        previewShares.stream()
            .map(share -> new PurchaseCalculator(share).calculateTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return saleValue.subtract(purchaseCost);
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

  private List<Share> createPreviewShares(BigDecimal quantity) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (quantity.compareTo(getQuantity()) > 0) {
      throw new IllegalArgumentException("Quantity cannot exceed owned quantity");
    }

    List<Share> previewShares = new ArrayList<>();
    BigDecimal remainingQuantity = quantity;
    for (Share share : shares) {
      if (remainingQuantity.compareTo(BigDecimal.ZERO) == 0) {
        break;
      }

      BigDecimal shareQuantity = share.getQuantity();
      if (remainingQuantity.compareTo(shareQuantity) >= 0) {
        previewShares.add(share);
        remainingQuantity = remainingQuantity.subtract(shareQuantity);
      } else {
        previewShares.add(new Share(share.getStock(), remainingQuantity, share.getPurchasePrice()));
        remainingQuantity = BigDecimal.ZERO;
      }
    }
    return previewShares;
  }
}
