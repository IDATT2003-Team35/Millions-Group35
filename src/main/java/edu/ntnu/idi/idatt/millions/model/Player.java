package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.model.transaction.TransactionArchive;
import edu.ntnu.idi.idatt.millions.util.Percentages;

import java.math.BigDecimal;

/**
 * Represents a player with a cash balance, a portfolio of shares, and a transaction archive.
 * The player starts with a fixed initial balance and can add or withdraw money later.
 */
public class Player {

  /** Minimum net worth multiplier (vs starting money) to qualify as SPECULATOR. */
  private static final BigDecimal SPECULATOR_MULTIPLIER = new BigDecimal("2");

  /** Minimum net worth multiplier (vs starting money) to qualify as INVESTOR. */
  private static final BigDecimal INVESTOR_MULTIPLIER = new BigDecimal("1.2");

  /** Minimum number of distinct trading weeks required for SPECULATOR rank. */
  private static final int SPECULATOR_MIN_WEEKS = 20;

  /** Minimum number of distinct trading weeks required for INVESTOR rank. */
  private static final int INVESTOR_MIN_WEEKS = 10;

  private String name;
  private BigDecimal startingMoney;
  private BigDecimal money;
  private Portfolio portfolio;
  private TransactionArchive transactionArchive;

  /**
   * Creates a new player with a name and starting balance.
   *
   * @param name the player's name, must be non-blank and max 50 characters
   * @param startingMoney the initial balance, must be greater than zero
   * @throws IllegalArgumentException if name or startingMoney is invalid
   */
  public Player(String name, BigDecimal startingMoney) {
    this(name, startingMoney, startingMoney);
  }

  /**
   * Creates a player with a name, starting balance, and current balance.
   *
   * <p>This constructor is used when restoring a saved game where the player's
   * current money may differ from the starting money.</p>
   *
   * @param name the player's name, must be non-blank and max 50 characters
   * @param startingMoney the player's original starting balance, must be positive
   * @param money the player's current balance, must not be negative
   * @throws IllegalArgumentException if name, startingMoney, or money is invalid
   */
  public Player(String name, BigDecimal startingMoney, BigDecimal money) {
    if (name == null || name.isBlank() || name.length() > 50){
      throw new IllegalArgumentException("Name cannot be empty or longer than 50 characters");
    }
    if (startingMoney == null || startingMoney.compareTo(BigDecimal.ZERO) <= 0){
      throw new IllegalArgumentException("Starting money cannot be null or less than 0");
    }
    if (money == null || money.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Money cannot be null or negative");
    }
    this.name = name;
    this.startingMoney = startingMoney;
    this.money = money;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
  }

  /**
   * Returns the player's name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the player's current balance.
   *
   * @return the current balance
   */
  public BigDecimal getMoney() {
    return money;
  }

  /**
   * Returns the player's starting balance.
   *
   * @return the starting balance
   */
  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  /**
   * Adds money to the player's balance.
   *
   * @param amount the amount to add, must be greater than zero
   * @throws IllegalArgumentException if amount is null or not positive
   */
  public void addMoney(BigDecimal amount){
    if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
      throw new IllegalArgumentException("Amount must be positive");
    }
    money = money.add(amount);
  }

  /**
   * Withdraws money from the player's balance.
   *
   * @param amount the amount to withdraw, must be greater than zero and not exceed balance
   * @throws IllegalArgumentException if amount is null, not positive, or exceeds balance
   */
  public void withdrawMoney(BigDecimal amount){
    if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
      throw new IllegalArgumentException("Amount must be positive");
    }
    if(money.compareTo(amount) < 0){
      throw new IllegalArgumentException("Balance is less than amount");
    }
    money = money.subtract(amount);
  }

  /**
   * Returns the player's total net worth.
   *
   * @return cash balance plus portfolio value
   */
  public BigDecimal getNetWorth(){
    return money.add(portfolio.getNetWorth());
  }


  /**
   * Returns the player's rank based on net worth and transaction history.
   *
   * @return the player's current rank
   */
  public PlayerRank getStatus() {
    int weekAmount = transactionArchive.countDistinctWeeks();
    BigDecimal currentNetWorth = getNetWorth();
    BigDecimal speculatorThreshold = startingMoney.multiply(SPECULATOR_MULTIPLIER);
    BigDecimal investorThreshold = startingMoney.multiply(INVESTOR_MULTIPLIER);

    if (currentNetWorth.compareTo(speculatorThreshold) >= 0
        && weekAmount >= SPECULATOR_MIN_WEEKS) {
      return PlayerRank.SPECULATOR;
    }

    if (currentNetWorth.compareTo(investorThreshold) >= 0
        && weekAmount >= INVESTOR_MIN_WEEKS) {
      return PlayerRank.INVESTOR;
    }

    return PlayerRank.NOVICE;
  }

  /**
   * Returns the player's total gain/loss as a percentage change from the
   * starting balance to the current net worth.
   *
   * @return the percentage change (positive for gain, negative for loss)
   */
  public BigDecimal getTotalGainLossPercent() {
    return Percentages.change(startingMoney, getNetWorth());
  }

  /**
   * Returns the player's total gain or loss as a cash amount.
   *
   * @return current net worth minus starting money (positive for gain, negative for loss)
   */
  public BigDecimal getTotalGainLoss() {
    return getNetWorth().subtract(startingMoney);
  }

  /**
   * Returns the player's portfolio.
   *
   * @return the portfolio
   */
  public Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * Returns the player's transaction archive.
   *
   * @return the transaction archive
   */
  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }
}
