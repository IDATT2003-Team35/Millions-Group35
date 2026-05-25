package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.factory.TransactionFactory;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Represents a stock exchange where players can buy and sell shares. Stocks are stored internally
 * by symbol, and the week advances via {@link #advance()}.
 */
public class Exchange {

  /**
   * Expected weekly drift (μ) in the GBM price model. 0.005 = 0.5% expected weekly return (~28%
   * annualized). Shared across all difficulty levels; only volatility varies.
   */
  private static final double DRIFT = 0.005;

  /**
   * Default weekly volatility (σ) in the GBM price model, used when no difficulty is supplied. 0.10
   * = 10% weekly standard deviation of log returns, matching {@link Difficulty#NORMAL}.
   */
  private static final double DEFAULT_VOLATILITY = 0.10;

  /** Lower floor on stock prices to prevent rounding artifacts. */
  private static final BigDecimal PRICE_FLOOR = BigDecimal.valueOf(0.01);

  private final String name;
  private int week;
  private final double volatility;
  private final Map<String, Stock> stockMap;
  private final Random random;

  /**
   * Creates a new Exchange with default volatility ({@link #DEFAULT_VOLATILITY}). Week starts at 1.
   *
   * @param name the name of the exchange, must not be null or blank
   * @param stocks the list of stocks available, must not be null
   * @throws IllegalArgumentException if name is null/blank or stocks is null
   */
  public Exchange(String name, List<Stock> stocks) {
    this(name, stocks, 1, DEFAULT_VOLATILITY);
  }

  /**
   * Creates a new Exchange with the given volatility. Week starts at 1.
   *
   * @param name the name of the exchange, must not be null or blank
   * @param stocks the list of stocks available, must not be null
   * @param volatility the weekly volatility (σ) for the GBM price model; must be positive
   *     (typically 0.05–0.20)
   * @throws IllegalArgumentException if any argument is invalid
   */
  public Exchange(String name, List<Stock> stocks, double volatility) {
    this(name, stocks, 1, volatility);
  }

  /**
   * Creates an exchange with a restored week number and default volatility.
   *
   * <p>This constructor is used when loading a saved game where the exchange should continue from a
   * previously saved week.
   *
   * @param name the name of the exchange, must not be null or blank
   * @param stocks the list of stocks available, must not be null
   * @param week the current trading week, must be positive
   * @throws IllegalArgumentException if name, stocks, or week is invalid
   */
  public Exchange(String name, List<Stock> stocks, int week) {
    this(name, stocks, week, DEFAULT_VOLATILITY);
  }

  /**
   * Creates an exchange with a restored week number and custom volatility.
   *
   * <p>This is the canonical constructor used by the {@link Difficulty}-aware game setup: callers
   * should pass {@code difficulty.getVolatility()} as the volatility argument.
   *
   * @param name the name of the exchange, must not be null or blank
   * @param stocks the list of stocks available, must not be null
   * @param week the current trading week, must be positive
   * @param volatility the weekly volatility (σ) for the GBM price model; must be positive
   *     (typically 0.05–0.20)
   * @throws IllegalArgumentException if any argument is invalid
   */
  public Exchange(String name, List<Stock> stocks, int week, double volatility) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be empty");
    }
    if (stocks == null) {
      throw new IllegalArgumentException("Stocks cannot be null");
    }
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }
    if (volatility <= 0) {
      throw new IllegalArgumentException("Volatility must be positive");
    }
    this.name = name;
    this.week = week;
    this.volatility = volatility;
    this.random = new Random();
    this.stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSymbol, stock -> stock));
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
      throw new IllegalArgumentException("Symbol cannot be null or blank");
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
   * Returns all stocks listed on this exchange.
   *
   * @return a list of all stocks
   */
  public List<Stock> getStocks() {
    return new ArrayList<>(stockMap.values());
  }

  /**
   * Finds all stocks whose symbol or company name contains the given search term. The search is
   * case-insensitive.
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
        .filter(
            s ->
                s.getSymbol().toLowerCase().contains(searchTermLowerCase)
                    || s.getCompany().toLowerCase().contains(searchTermLowerCase))
        .collect(Collectors.toList());
  }

  /**
   * Buys shares of a stock for a player.
   *
   * @param symbol the stock symbol to buy; must be listed on this exchange
   * @param quantity the number of shares to buy; must not be null or negative
   * @param player the player making the purchase; must not be null
   * @return the resulting {@link Purchase} transaction
   * @throws IllegalArgumentException if the stock is not found, quantity is invalid, or player is
   *     null
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (!hasStock(symbol)) {
      throw new IllegalArgumentException("Stock does not exist with symbol: " + symbol);
    }
    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());
    Purchase purchase = TransactionFactory.createPurchase(share, week);
    purchase.commit(player);
    return purchase;
  }

  /**
   * Sells a share for a player.
   *
   * @param share the share to sell; must not be null
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
    Sale sale = TransactionFactory.createSale(share, week);
    sale.commit(player);
    return sale;
  }

  /**
   * Sells a quantity of one stock for a player.
   *
   * <p>If the quantity spans multiple purchase lots, one sale transaction is created per affected
   * lot.
   *
   * @param symbol the stock symbol to sell; must be listed on this exchange
   * @param quantity the quantity to sell; must be greater than zero and no larger than the player's
   *     owned quantity
   * @param player the player making the sale; must not be null
   * @return the completed sale transactions created by the sale
   * @throws IllegalArgumentException if the symbol, quantity, or player is invalid
   * @throws IllegalStateException if the player does not own shares with the symbol
   */
  public List<Transaction> sell(String symbol, BigDecimal quantity, Player player) {
    validateQuantitySaleInput(symbol, quantity, player);
    List<Share> ownedShares = player.getPortfolio().getShares(symbol);
    if (ownedShares.isEmpty()) {
      throw new IllegalStateException("Player does not own shares with symbol: " + symbol);
    }

    if (quantity.compareTo(getTotalQuantity(ownedShares)) > 0) {
      throw new IllegalArgumentException("Quantity cannot exceed owned quantity");
    }

    List<Transaction> sales = new ArrayList<>();
    BigDecimal remainingQuantity = quantity;
    for (Share ownedShare : ownedShares) {
      if (remainingQuantity.compareTo(BigDecimal.ZERO) == 0) {
        break;
      }

      BigDecimal shareQuantity = ownedShare.getQuantity();
      if (remainingQuantity.compareTo(shareQuantity) >= 0) {
        sales.add(sellFullShare(ownedShare, player));
        remainingQuantity = remainingQuantity.subtract(shareQuantity);
      } else {
        sales.add(sellPartialShare(ownedShare, remainingQuantity, player));
        remainingQuantity = BigDecimal.ZERO;
      }
    }
    return sales;
  }

  private void validateQuantitySaleInput(String symbol, BigDecimal quantity, Player player) {
    if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (!hasStock(symbol)) {
      throw new IllegalArgumentException("Stock does not exist with symbol: " + symbol);
    }
  }

  private BigDecimal getTotalQuantity(List<Share> shares) {
    return shares.stream().map(Share::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private Transaction sellFullShare(Share share, Player player) {
    Sale sale = TransactionFactory.createSale(share, week);
    sale.commit(player);
    return sale;
  }

  private Transaction sellPartialShare(Share originalShare, BigDecimal quantity, Player player) {
    Share soldShare =
        new Share(originalShare.getStock(), quantity, originalShare.getPurchasePrice());
    Share remainingShare =
        new Share(
            originalShare.getStock(),
            originalShare.getQuantity().subtract(quantity),
            originalShare.getPurchasePrice());

    boolean replaced =
        player.getPortfolio().replaceShare(originalShare, List.of(soldShare, remainingShare));
    if (!replaced) {
      throw new IllegalStateException("Player does not have this share");
    }
    Sale sale = TransactionFactory.createSale(soldShare, week);
    sale.commit(player);
    return sale;
  }

  /**
   * Advances to the next trading week and updates each stock's price using Geometric Brownian
   * Motion (GBM).
   *
   * <p>The price evolves according to the discrete GBM step formula:
   *
   * <pre>
   *   S_{t+1} = S_t * exp[(μ − σ²/2) + σ·Z],   Z ~ N(0,1)
   * </pre>
   *
   * derived from the GBM stochastic differential equation {@code dS_t = μS_t dt + σS_t dW_t} with
   * Δt = 1 week. The {@code −σ²/2} correction term in the exponent compensates for volatility drag
   * (a consequence of Jensen's inequality on multiplicative noise) — without it, symmetric random
   * returns would cause all prices to drift toward zero over time. A floor of {@link #PRICE_FLOOR}
   * prevents rounding artifacts from producing non-positive prices.
   */
  public void advance() {
    week++;
    final double driftAdjusted = DRIFT - 0.5 * volatility * volatility;
    for (Stock stock : stockMap.values()) {
      BigDecimal currentPrice = stock.getSalesPrice();
      double z = random.nextGaussian();
      double exponent = driftAdjusted + volatility * z;
      double multiplier = Math.exp(exponent);
      BigDecimal newPrice =
          currentPrice.multiply(BigDecimal.valueOf(multiplier)).setScale(2, RoundingMode.HALF_UP);
      newPrice = newPrice.max(PRICE_FLOOR);
      stock.addNewSalesPrice(newPrice);
    }
  }

  /**
   * Returns the weekly volatility (σ) used by the GBM price model for this exchange. Set at
   * construction time (typically from {@link Difficulty#getVolatility()}).
   *
   * @return the weekly volatility
   */
  public double getVolatility() {
    return volatility;
  }

  /**
   * Retrieves a list of the top gaining stocks, sorted by the highest positive percent change.
   *
   * @param limit the maximum number of stocks to return; must be positive
   * @return a list of gaining stocks
   * @throws IllegalArgumentException if limit is less than or equal to zero
   */
  public List<Stock> getGainers(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }

    return stockMap.values().stream()
        .filter(stock -> stock.getLatestPercentChange().compareTo(BigDecimal.ZERO) > 0)
        .sorted(Comparator.comparing(Stock::getLatestPercentChange).reversed())
        .limit(limit)
        .toList();
  }

  /**
   * Retrieves a list of the top losing stocks, sorted by the lowest negative percent change.
   *
   * @param limit the maximum number of stocks to return; must be positive
   * @return a list of losing stocks
   * @throws IllegalArgumentException if limit is less than or equal to zero
   */
  public List<Stock> getLosers(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }

    return stockMap.values().stream()
        .filter(stock -> stock.getLatestPercentChange().compareTo(BigDecimal.ZERO) < 0)
        .sorted(Comparator.comparing(Stock::getLatestPercentChange))
        .limit(limit)
        .toList();
  }
}
