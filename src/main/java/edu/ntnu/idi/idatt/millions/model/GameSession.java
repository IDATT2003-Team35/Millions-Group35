package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.observer.Subject;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the active game session used by the GUI.
 * Coordinates player and exchange operations, records net worth snapshots,
 * and notifies observers after successful changes.
 */
public class GameSession implements Subject {
  private final Player player;
  private final Exchange exchange;
  private final List<Observer> observers;
  private final NetWorthHistory netWorthHistory;

  /**
   * Creates a new game session.
   *
   * @param player the active player
   * @param exchange the active exchange
   * @throws IllegalArgumentException if player or exchange is null
   */
  public GameSession(Player player, Exchange exchange) {
    this(player, exchange, null);
  }

  /**
   * Creates a game session from saved game state.
   *
   * @param player the active player
   * @param exchange the active exchange
   * @param savedNetWorthHistory the saved net worth history, or null to record current net worth
   * @throws IllegalArgumentException if player or exchange is null
   */
  public GameSession(Player player, Exchange exchange, List<BigDecimal> savedNetWorthHistory) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (exchange == null) {
      throw new IllegalArgumentException("Exchange cannot be null");
    }
    this.player = player;
    this.exchange = exchange;
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
   * Advances the game to the next week and notifies observers.
   */
  public void advanceWeek() {
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

  /**
   * Records the player's current net worth for the current exchange week.
   */
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
