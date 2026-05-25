package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.observer.Subject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the active game session used by the GUI. Coordinates player and exchange operations,
 * records net worth snapshots, and notifies observers after successful changes.
 */
public class GameSession implements Subject {
  private final Player player;
  private final Exchange exchange;
  private final Difficulty difficulty;
  private final GameMode mode;
  private final List<Observer> observers;
  private final NetWorthHistory netWorthHistory;

  /**
   * Creates a new game session with default settings ({@link Difficulty#NORMAL}, {@link
   * GameMode#SANDBOX}).
   *
   * @param player the active player
   * @param exchange the active exchange
   * @throws IllegalArgumentException if player or exchange is null
   */
  public GameSession(Player player, Exchange exchange) {
    this(player, exchange, Difficulty.NORMAL, GameMode.SANDBOX, null);
  }

  /**
   * Creates a new game session with the given difficulty and mode.
   *
   * @param player the active player
   * @param exchange the active exchange
   * @param difficulty the difficulty level
   * @param mode the game mode
   * @throws IllegalArgumentException if any argument is null
   */
  public GameSession(Player player, Exchange exchange, Difficulty difficulty, GameMode mode) {
    this(player, exchange, difficulty, mode, null);
  }

  /**
   * Creates a game session from saved game state with default settings ({@link Difficulty#NORMAL},
   * {@link GameMode#SANDBOX}). Used for loading legacy save files that predate the difficulty/mode
   * feature.
   *
   * @param player the active player
   * @param exchange the active exchange
   * @param savedNetWorthHistory the saved net worth history, or null to record current net worth
   * @throws IllegalArgumentException if player or exchange is null
   */
  public GameSession(Player player, Exchange exchange, List<BigDecimal> savedNetWorthHistory) {
    this(player, exchange, Difficulty.NORMAL, GameMode.SANDBOX, savedNetWorthHistory);
  }

  /**
   * Canonical constructor: creates a game session with explicit settings and optional saved net
   * worth history.
   *
   * @param player the active player
   * @param exchange the active exchange
   * @param difficulty the difficulty level
   * @param mode the game mode
   * @param savedNetWorthHistory the saved net worth history, or null to record current net worth
   * @throws IllegalArgumentException if any required argument is null
   */
  public GameSession(
      Player player,
      Exchange exchange,
      Difficulty difficulty,
      GameMode mode,
      List<BigDecimal> savedNetWorthHistory) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (exchange == null) {
      throw new IllegalArgumentException("Exchange cannot be null");
    }
    if (difficulty == null) {
      throw new IllegalArgumentException("Difficulty cannot be null");
    }
    if (mode == null) {
      throw new IllegalArgumentException("Mode cannot be null");
    }
    this.player = player;
    this.exchange = exchange;
    this.difficulty = difficulty;
    this.mode = mode;
    this.observers = new ArrayList<>();
    if (savedNetWorthHistory == null || savedNetWorthHistory.isEmpty()) {
      this.netWorthHistory = new NetWorthHistory();
      recordNewNetWorthPoint();
    } else {
      this.netWorthHistory = new NetWorthHistory(savedNetWorthHistory);
    }
  }

  /**
   * Returns the active player.
   *
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the active exchange.
   *
   * @return the exchange
   */
  public Exchange getExchange() {
    return exchange;
  }

  /**
   * Returns the difficulty level of this session.
   *
   * @return the difficulty
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * Returns the game mode of this session.
   *
   * @return the game mode
   */
  public GameMode getMode() {
    return mode;
  }

  /**
   * Checks whether the game has reached its end condition.
   *
   * <p>For {@link GameMode#SANDBOX} this always returns {@code false} (no time limit). For {@link
   * GameMode#CHALLENGE} this returns {@code true} once the exchange week reaches the mode's week
   * limit (e.g. week 52 for the standard 52-week challenge).
   *
   * @return {@code true} if the game should end, {@code false} otherwise
   */
  public boolean isGameOver() {
    return mode.hasWeekLimit() && exchange.getWeek() >= mode.getWeekLimit();
  }

  /**
   * Buys stock for the active player and notifies observers if successful.
   *
   * @param symbol the stock symbol to buy
   * @param quantity the number of shares to buy
   * @return the committed purchase transaction
   */
  public Transaction buyStock(String symbol, BigDecimal quantity) {
    Transaction transaction = exchange.buy(symbol, quantity, player);
    recordNewNetWorthPoint();
    notifyObservers();
    return transaction;
  }

  /**
   * Sells a share for the active player and notifies observers if successful.
   *
   * @param share the share to sell
   * @return the committed sale transaction
   */
  public Transaction sellShare(Share share) {
    Transaction transaction = exchange.sell(share, player);
    recordNewNetWorthPoint();
    notifyObservers();
    return transaction;
  }

  /**
   * Sells a quantity of a stock for the active player and notifies observers if successful.
   *
   * @param symbol the stock symbol to sell
   * @param quantity the quantity to sell
   * @return the committed sale transactions created by the sale
   */
  public List<Transaction> sellStock(String symbol, BigDecimal quantity) {
    List<Transaction> transactions = exchange.sell(symbol, quantity, player);
    recordNewNetWorthPoint();
    notifyObservers();
    return transactions;
  }

  /**
   * Advances the game to the next week and notifies observers.
   *
   * @throws IllegalStateException if the game is already over (see {@link #isGameOver()})
   */
  public void advanceWeek() {
    if (isGameOver()) {
      throw new IllegalStateException(
          "Cannot advance: game is already over (week "
              + exchange.getWeek()
              + " of "
              + mode.getWeekLimit()
              + ")");
    }
    exchange.advance();
    recordNewNetWorthPoint();
    notifyObservers();
  }

  /**
   * Returns the recorded net worth history for the active player.
   *
   * @return a copy of the net worth history
   */
  public List<BigDecimal> getNetWorthHistory() {
    return netWorthHistory.getHistory();
  }

  /** Records the player's current net worth for the current exchange week. */
  private void recordNewNetWorthPoint() {
    netWorthHistory.recordNewPoint(getExchange().getWeek(), getPlayer().getNetWorth());
  }

  @Override
  public void addObserver(Observer observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Observer cannot be null");
    }
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (Observer observer : observers) {
      observer.update();
    }
  }
}
