package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.TransactionCalculator;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;

/**
 * Represents a financial transaction (e.g., purchase or sale).
 * Ensures the transaction is only committed once and handles archiving.
 */
public abstract class Transaction {
  private final Share share;
  private final int week;
  private final TransactionCalculator calculator;
  private boolean committed;

  /**
   * Constructs a new transaction.
   *
   * @param share the associated share
   * @param week the week of the transaction
   * @param calculator the calculator for financial details
   * @throws IllegalArgumentException if arguments are null or week is invalid
   */
  protected Transaction(Share share, int week, TransactionCalculator calculator) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }
    if (calculator == null) {
      throw new IllegalArgumentException("Calculator cannot be null");
    }

    this.share = share;
    this.week = week;
    this.calculator = calculator;
    this.committed = false;
  }

  /**
   * @return the associated share
   */
  public Share getShare() {
    return share;
  }

  /**
   * @return the transaction week
   */
  public int getWeek() {
    return week;
  }

  /**
   * @return the transaction calculator
   */
  public TransactionCalculator getCalculator() {
    return calculator;
  }

  /**
   * @return true if the transaction has been committed, false otherwise
   */
  public boolean isCommitted() {
    return committed;
  }

  /**
   * Commits the transaction for the given player.
   * Marks it as committed and adds it to the player's archive.
   *
   * @param player the player executing the transaction
   * @throws IllegalStateException if already committed
   * @throws IllegalArgumentException if player is null
   */
  public void commit(Player player) {
    if (committed) {
      throw new IllegalStateException("transaction has already been committed");
    }
    if (player == null) {
      throw new IllegalArgumentException("player cannot be null");
    }

    executeTransaction(player);

    player.getTransactionArchive().add(this);
    this.committed = true;
  }

  /**
   * Executes the specific logic for the transaction type.
   *
   * @param player the player involved
   */
  protected abstract void executeTransaction(Player player);
}