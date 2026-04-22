package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.observer.Subject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the active game session used by the GUI.
 * Coordinates player and exchange operations and notifies observers after successful changes.
 */
public class GameSession implements Subject {
  private final Player player;
  private final Exchange exchange;
  private final List<Observer> observers;

  /**
   * Creates a new game session.
   *
   * @param player the active player
   * @param exchange the active exchange
   * @throws IllegalArgumentException if player or exchange is null
   */
  public GameSession(Player player, Exchange exchange) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (exchange == null) {
      throw new IllegalArgumentException("Exchange cannot be null");
    }
    this.player = player;
    this.exchange = exchange;
    this.observers = new ArrayList<>();
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
   */
  public void buyStock(String symbol, BigDecimal quantity) {
    exchange.buy(symbol, quantity, player);
    notifyObservers();
  }

  /**
   * Sells a share for the active player and notifies observers if successful.
   *
   * @param share the share to sell
   */
  public void sellShare(Share share) {
    exchange.sell(share, player);
    notifyObservers();
  }

  /**
   * Advances the game to the next week and notifies observers.
   */
  public void advanceWeek() {
    exchange.advance();
    notifyObservers();
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
