package edu.ntnu.idi.idatt.millions.factory;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionFactoryTest {
  private Share share;
  private int week;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));
    share = new Share(stock, new BigDecimal("5"), new BigDecimal("200.00"));
    week = 1;
  }

  @Test
  void createPurchaseReturnsPurchaseWithCorrectValues() {
    Transaction tran = TransactionFactory.createPurchase(share, week);

    assertInstanceOf(Purchase.class, tran);
    assertEquals(share, tran.getShare());
    assertEquals(week, tran.getWeek());
    assertFalse(tran.isCommitted());
  }

  @Test
  void createPurchaseWithNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
            () -> TransactionFactory.createPurchase(null, week));
  }

  @Test
  void createPurchaseWithInvalidWeekThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
            () -> TransactionFactory.createPurchase(share, 0));
  }

  @Test
  void createSaleReturnsSaleWithCorrectValues() {
    Transaction tran = TransactionFactory.createSale(share, week);

    assertInstanceOf(Sale.class, tran);
    assertEquals(share, tran.getShare());
    assertEquals(week, tran.getWeek());
    assertFalse(tran.isCommitted());
  }

  @Test
  void createSaleWithNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
            () -> TransactionFactory.createSale(null, week));
  }

  @Test
  void createSaleWithInvalidWeekThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
            () -> TransactionFactory.createSale(share, 0));
  }
}