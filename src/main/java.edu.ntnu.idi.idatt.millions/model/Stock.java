package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stock with a symbol, company name and a history of sales prices.
 * Inputs are validated to ensure non-null and reasonable lengths/values.
 */
public class Stock {

  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  /**
   * Create a new Stock.
   *
   * @param symbol the stock symbol, non-null, non-blank, max 20 characters
   * @param company the company name, non-null, non-blank, max 100 characters
   * @param salesPrice the initial sales price, non-null and not negative
   * @throws IllegalArgumentException if any validation fails
   */
  public Stock(String symbol, String company, BigDecimal salesPrice) {
    if (symbol == null || symbol.isBlank() || symbol.length() > 20) {
      throw new IllegalArgumentException("Symbol must be non-empty and not longer than 20 characters.");
    }
    if (company == null || company.isBlank() || company.length() > 100) {
      throw new IllegalArgumentException("Company name must be non-empty and not longer than 100 characters.");
    }
    if (salesPrice == null || salesPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Initial price cannot be negative.");
    }
    this.symbol = symbol;
    this.company = company;
    // Use an ArrayList to store price history
    this.prices = new ArrayList<>();
    this.prices.add(salesPrice);
  }

  /**
   * Get the stock symbol.
   *
   * @return the symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Get the company name.
   *
   * @return the company name
   */
  public String getCompany() {
    return company;
  }

  /**
   * Get the most recent sales price.
   *
   * @return the latest price
   * @throws IllegalStateException if there are no recorded prices
   */
  public BigDecimal getSalesPrice() {
    if (prices.isEmpty()) {
      throw new IllegalStateException("No sales price available.");
    }
    // Return the last element in the list (most recent price).
    return prices.get(prices.size() - 1);
  }

  /**
   * Add a new sales price to the history.
   *
   * @param price the new price, non-null and not negative
   * @throws IllegalArgumentException if price is null or negative
   */
  public void addNewSalesPrice(BigDecimal price) {
    if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("New price cannot be negative");
    }
    prices.add(price);
  }
}