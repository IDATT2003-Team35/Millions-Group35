package edu.ntnu.idi.idatt.millions.model.transaction;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionArchiveTest {
  private TransactionArchive archive;
  private Purchase purchaseW1;
  private Sale saleW1;
  private Purchase purchaseW2;
  private Stock stock;
  private Share share;

  @BeforeEach
  void setUp() {
    archive = new TransactionArchive();
    stock = new Stock("TST", "Test", new BigDecimal("10.00"));
    share = new Share(stock, BigDecimal.ONE, BigDecimal.TEN);
    purchaseW1 = new Purchase(share, 1);
    saleW1 = new Sale(share, 1);
    purchaseW2 = new Purchase(share, 2);
  }

  @Test
  void addUpdatesEmptyStateAndRejectsNull() {
    assertTrue(archive.isEmpty());
    archive.add(purchaseW1);
    assertFalse(archive.isEmpty());
    assertFalse(archive.add(null));
  }

  @Test
  void getTransactionsByWeekReturnsMatchingTransactions() {
    archive.add(purchaseW1);
    archive.add(saleW1);
    archive.add(purchaseW2);

    List<Transaction> week1 = archive.getTransactions(1);
    assertEquals(2, week1.size());

    assertThrows(IllegalArgumentException.class, () -> archive.getTransactions(0));
  }

  @Test
  void getSpecificTransactionTypesReturnsExpectedLists() {
    archive.add(purchaseW1);
    archive.add(saleW1);

    List<Purchase> purchases = archive.getPurchases(1);
    assertEquals(1, purchases.size());
    assertEquals(purchaseW1, purchases.get(0));

    List<Sale> sales = archive.getSales(1);
    assertEquals(1, sales.size());
    assertEquals(saleW1, sales.get(0));
  }

  @Test
  void countDistinctWeeksReturnsNumberOfUniqueWeeks() {
    archive.add(purchaseW1);
    archive.add(saleW1); // same week
    archive.add(purchaseW2); // new week

    assertEquals(2, archive.countDistinctWeeks());
  }

  @Test
  void transactionConstructorInvalidInputThrowsException() {
    // Testing the protected constructor logic via a subclass (Purchase)
    assertThrows(NullPointerException.class, () -> new Purchase(null, 1));
    assertThrows(IllegalArgumentException.class, () -> new Purchase(share, 0));
    assertThrows(IllegalArgumentException.class, () -> new Purchase(share, -5));
  }

  @Test
  void invalidWeekQueriesThrowIllegalArgumentException() {
    TransactionArchive archive = new TransactionArchive();

    // Attempting to query negative or zero weeks
    assertThrows(IllegalArgumentException.class, () -> archive.getTransactions(0));
    assertThrows(IllegalArgumentException.class, () -> archive.getPurchases(-1));
    assertThrows(IllegalArgumentException.class, () -> archive.getSales(0));
  }

  @Test
  void addNullTransactionReturnsFalse() {
    TransactionArchive archive = new TransactionArchive();
    assertFalse(archive.add(null));
  }
}
