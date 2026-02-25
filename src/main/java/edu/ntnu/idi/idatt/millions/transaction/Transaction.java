package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.TransactionCalculator;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;

public abstract class Transaction {
  private final Share share;
  private final int week;
  private final TransactionCalculator calculator;
  private boolean committed;

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

  public Share getShare() {
    return share;
  }

  public int getWeek() {
    return week;
  }

  public TransactionCalculator getCalculator() {
    return calculator;
  }

  public boolean isCommitted() {
    return committed;
  }

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

  protected abstract void executeTransaction(Player player);
}
