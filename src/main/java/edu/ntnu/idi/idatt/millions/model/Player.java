package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.transaction.TransactionArchive;

import java.math.BigDecimal;

/**
 * Represents a player with a cash balance, a portfolio of shares, and a transaction archive.
 * The player starts with a fixed initial balance and can add or withdraw money later.
 */
public class Player {
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
  public Player(String name, BigDecimal startingMoney){
    if(name == null || name.isBlank() || name.length() > 50){
      throw new IllegalArgumentException("Name cannot be empty or longer than 50 characters");
    }
    if(startingMoney == null || startingMoney.compareTo(BigDecimal.ZERO) <= 0){
      throw new IllegalArgumentException("Starting money cant null or less than 0");
    }
    this.name = name;
    this.startingMoney = startingMoney;
    this.portfolio = new Portfolio();
    this.money = startingMoney;
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


  public String getStatus() {
    int weekAmount = transactionArchive.countDistinctWeeks();
    BigDecimal currentNetWorth = getNetWorth();
    BigDecimal speculatorInc = startingMoney.multiply(new BigDecimal("2"));
    BigDecimal investorInc = startingMoney.multiply(new BigDecimal("1.2"));

    if (currentNetWorth.compareTo(speculatorInc) >= 0 && weekAmount >= 20) {
      return "Speculator";
    }

    if (currentNetWorth.compareTo(investorInc) >= 0 && weekAmount >= 10) {
      return "Investor";
    }

    return "Novice";
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
