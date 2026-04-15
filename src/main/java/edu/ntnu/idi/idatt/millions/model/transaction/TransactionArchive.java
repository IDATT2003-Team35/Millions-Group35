package edu.ntnu.idi.idatt.millions.model.transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Archive for storing and retrieving completed transactions.
 */
public class TransactionArchive {
  private final List<Transaction> transactions;

  /**
   * Constructs a new, empty transaction archive.
   */
  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  /**
   * Adds a transaction to the archive.
   *
   * @param transaction the transaction to add
   * @return true if added successfully, false if transaction is null
   */
  public boolean add(Transaction transaction) {
    if (transaction == null) {
      return false;
    }
    return transactions.add(transaction);
  }

  /**
   * Checks if the archive is empty.
   *
   * @return true if there are no transactions, false otherwise
   */
  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  /**
   * Retrieves all transactions for a specific week.
   *
   * @param week the week number
   * @return a list of transactions from that week
   * @throws IllegalArgumentException if week is invalid
   */
  public List<Transaction> getTransactions(int week) {
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }

    return transactions.stream()
            .filter(t -> t.getWeek() == week)
            .toList();
  }

  /**
   * Retrieves all purchase transactions for a specific week.
   *
   * @param week the week number
   * @return a list of purchases from that week
   * @throws IllegalArgumentException if week is invalid
   */
  public List<Purchase> getPurchases(int week) {
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }

    return transactions.stream()
            .filter(t -> t.getWeek() == week && t instanceof Purchase)
            .map(t -> (Purchase) t)
            .toList();
  }

  /**
   * Retrieves all sale transactions for a specific week.
   *
   * @param week the week number
   * @return a list of sales from that week
   * @throws IllegalArgumentException if week is invalid
   */
  public List<Sale> getSales(int week) {
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }

    return transactions.stream()
            .filter(t -> t.getWeek() == week && t instanceof Sale)
            .map(t -> (Sale) t)
            .toList();
  }

  /**
   * Counts the number of distinct weeks that have recorded transactions.
   *
   * @return the number of distinct weeks
   */
  public int countDistinctWeeks() {
    return (int) transactions.stream()
            .map(Transaction::getWeek)
            .distinct()
            .count();
  }
}
