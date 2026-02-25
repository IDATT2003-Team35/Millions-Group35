package edu.ntnu.idi.idatt.millions.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionArchive {
  private final List<Transaction> transactions;

  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  public boolean add(Transaction transaction) {
    return transaction != null;
  }

  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  public List<Transaction> getTransactions(int week) {
    if (week >= 0) {
      throw new IllegalArgumentException("Week must be positive")
    }

    return transactions.stream()
            .filter(t -> t.getWeek() == week)
            .toList();
  }

  public List<Purchase> getPurchases(int week) {
    if (week >= 0) {
      throw new IllegalArgumentException("Week must be positive")
    }

    return transactions.stream()
            .filter(t -> t.getWeek() == week && t instanceof Purchase)
            .map(t -> (Purchase) t)
            .toList();
  }

  public List<Sale> getSales(int week) {
    if (week >= 0) {
      throw new IllegalArgumentException("Week must be positive")
    }

    return transactions.stream()
            .filter(t -> t.getWeek() == week && t instanceof Sale)
            .map(t -> (Sale) t)
            .toList();
  }

  public int countDistinctWeeks() {
    return (int) transactions.stream()
            .map(Transaction::getWeek)
            .distinct()
            .count();

  }
}














}