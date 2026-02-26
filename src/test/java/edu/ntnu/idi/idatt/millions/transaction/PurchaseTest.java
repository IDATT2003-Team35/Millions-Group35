package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTest {
  private Player player;
  private Share share;
  private Stock stock;

  @BeforeEach
  void setUp() {
    player = new Player("Test Player", new BigDecimal("2000.00"));
    stock = new Stock("AAPL", "Apple", new BigDecimal("150.00"));
    // 10 shares at 150 = 1500 gross. 0.5% commission = 7.50. Total = 1507.50
    share = new Share(stock, new BigDecimal("10"), new BigDecimal("150.00"));
  }

  @Test
  void testPurchaseConstructorAndGetters() {
    Purchase purchase = new Purchase(share, 1);
    assertEquals(share, purchase.getShare());
    assertEquals(1, purchase.getWeek());
    assertNotNull(purchase.getCalculator());
    assertFalse(purchase.isCommitted());
  }

  @Test
  void testPurchaseSuccessfulExecution() {
    Purchase purchase = new Purchase(share, 1);
    purchase.commit(player);

    assertEquals(new BigDecimal("492.50"), player.getMoney());
    assertTrue(player.getPortfolio().contains(share));
    assertTrue(purchase.isCommitted());
  }

  @Test
  void testPurchaseInsufficientFundsThrows() {
    Player poorPlayer = new Player("Poor Player", new BigDecimal("100.00"));
    Purchase purchase = new Purchase(share, 1);

    assertThrows(IllegalStateException.class, () -> purchase.commit(poorPlayer));
  }

  @Test
  void testPurchaseCommitTwiceThrows() {
    Purchase purchase = new Purchase(share, 1);
    purchase.commit(player);

    assertThrows(IllegalStateException.class, () -> purchase.commit(player));
  }
}