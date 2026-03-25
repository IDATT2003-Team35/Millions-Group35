package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.transaction.Sale;
import edu.ntnu.idi.idatt.millions.transaction.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Represents a stock exchange where players can buy and sell shares.
 * Stocks are stored internally by symbol, and the week advances via {@link #advance()}.
 */
public class Exchange {

  private final String name;
  private int week;
  private final Map<String, Stock> stockMap;
  private final Random random;

  /**
   * Creates a new Exchange.
   * Week starts at 1.
   *
   * @param name the name of the exchange, must not be null or blank
   * @param stocks the list of stocks available, must not be null
   * @throws IllegalArgumentException if name is null/blank or stocks is null
   */
  public Exchange(String name, List<Stock> stocks) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    if (stocks == null) {
      throw new IllegalArgumentException("Stocks cannot be null");
    }
    this.name = name;
    this.week = 1;
    this.random = new Random();
    this.stockMap = stocks.stream()
        .collect(Collectors.toMap(Stock::getSymbol, stock -> stock));
  }

  /**
   * Returns the name of this exchange.
   *
   * @return the exchange name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current trading week.
   *
   * @return the week number
   */
  public int getWeek() {
    return week;
  }

  /**
   * Checks whether a stock with the given symbol is listed on this exchange.
   *
   * @param symbol the stock symbol; must not be null or blank
   * @return {@code true} if the stock exists
   * @throws IllegalArgumentException if symbol is null or blank
   */
  public boolean hasStock(String symbol) {
    if (symbol == null || symbol.isBlank()) {
      throw new IllegalArgumentException("Symbol cannot be blank or empty");
    }
    return stockMap.containsKey(symbol);
  }

  /**
   * Returns the stock with the given symbol.
   *
   * @param symbol the stock symbol; must not be null or blank
   * @return the matching {@link Stock}
   * @throws IllegalArgumentException if the stock is not listed on this exchange
   */
  public Stock getStock(String symbol) {
    if (!hasStock(symbol)) {
      throw new IllegalArgumentException("Stock not found: " + symbol);
    }
    return stockMap.get(symbol);
  }

  /**
   * Finds all stocks whose symbol or company name contains the given search term.
   * The search is case-insensitive.
   *
   * @param searchTerm the term to search for; must not be null or blank
   * @return a list of matching stocks
   * @throws IllegalArgumentException if searchTerm is null or blank
   */
  public List<Stock> findStocks(String searchTerm) {
    if (searchTerm == null || searchTerm.isBlank()) {
      throw new IllegalArgumentException("Search term cannot be null or blank");
    }
    String searchTermLowerCase = searchTerm.toLowerCase();
    return stockMap.values().stream()
        .filter(s -> s.getSymbol().toLowerCase().contains(searchTermLowerCase)
            || s.getCompany().toLowerCase().contains(searchTermLowerCase))
        .collect(Collectors.toList());
  }

  /**
   * Buys shares of a stock for a player.
   *
   * @param symbol   the stock symbol to buy; must be listed on this exchange
   * @param quantity the number of shares to buy; must not be null or negative
   * @param player   the player making the purchase; must not be null
   * @return the resulting {@link Purchase} transaction
   * @throws IllegalArgumentException if the stock is not found, quantity is invalid,
   *                                  or player is null
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (!hasStock(symbol)){
      throw new IllegalArgumentException("Stock does not exist with symbol" + symbol);
    }
    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    Purchase purchase = new Purchase(share, week);
    purchase.commit(player);
    return purchase;
  }

  /**
   * Sells a share for a player.
   *
   * @param share  the share to sell; must not be null
   * @param player the player making the sale; must not be null
   * @return the resulting {@link Sale} transaction
   * @throws IllegalArgumentException if share or player is null
   */
  public Transaction sell(Share share, Player player) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (!player.getPortfolio().contains(share)) {
      throw new IllegalStateException("Player does not have this share");
    }
    Sale sale = new Sale(share, week);
    sale.commit(player);
    return sale;
  }

  /**
   * Advances to the next trading week and randomly updates each stock's price.
   * Each stocks price changes by +- 0-10% each week.
   * Also checks that stock price cant go below 0.01
   */
  public void advance() {
    week++;
    for (Stock stock : stockMap.values()) {
      BigDecimal currentPrice = stock.getSalesPrice();
      double changePercent = (random.nextDouble() * 0.2) - 0.1;
      BigDecimal change = currentPrice.multiply(BigDecimal.valueOf(changePercent));
      BigDecimal newPrice = currentPrice.add(change)
          .setScale(2, RoundingMode.HALF_UP);
      newPrice = newPrice.max(BigDecimal.valueOf(0.01));
      stock.addNewSalesPrice(newPrice);
    }
  }

  public List<Stock> getGainers(int limit) {
    return stockMap.values().stream()
            .filter(stock -> stock.getLatestPriceChange().compareTo(BigDecimal.ZERO) > 0)
            .sorted(Comparator.comparing(Stock::getLatestPriceChange).reversed())
            .limit(limit)
            .toList();
  }

  public List<Stock> getLosers(int limit) {
    return stockMap.values().stream()
            .filter(stock -> stock.getLatestPriceChange().compareTo(BigDecimal.ZERO) < 0)
            .sorted(Comparator.comparing(Stock::getLatestPriceChange))
            .limit(limit)
            .toList();
  }
}
