package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SaleTest {
  private Player player;
  private Share share;
  private Stock stock;

  @BeforeEach
  void setUp() {
    player = new Player("Test Player", new BigDecimal("2000.00"));
    stock = new Stock("AAPL", "Apple", new BigDecimal("100.00"));
    // Bought at 100, current market is 100. 10 shares.
    // Gross = 1000. Commission (1%) = 10. Tax = 0. Total = 990.00
    share = new Share(stock, new BigDecimal("10"), new BigDecimal("100.00"));
  }

  @Test
  void commitValidSaleUpdatesPlayerState() {
    player.getPortfolio().addShare(share);
    Sale sale = new Sale(share, 2);
    sale.commit(player);

    // 1000 + 990 = 1990.00
    assertEquals(new BigDecimal("2990.00"), player.getMoney());
    assertFalse(player.getPortfolio().contains(share));
  }

  @Test
  void commitWithoutOwnedShareThrowsIllegalStateException() {
    Sale sale = new Sale(share, 2);
    assertThrows(IllegalStateException.class, () -> sale.commit(player));
  }

  @Test
  void constructorAndCommitInvalidInputsThrowExceptions() {
    assertThrows(NullPointerException.class, () -> new Sale(null, 1));
    assertThrows(IllegalArgumentException.class, () -> new Sale(share, 0));

    Sale sale = new Sale(share, 1);
    assertThrows(IllegalArgumentException.class, () -> sale.commit(null));
  }

  @Test
  void purchaseCommitWithZeroBalanceThrowsIllegalStateException() {
    Player brokePlayer = new Player("Broke", new BigDecimal("0.01"));
    Purchase purchase = new Purchase(share, 1);

    IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> purchase.commit(brokePlayer));
    assertEquals("Player does not have enough money for purchase", exception.getMessage());
  }

  @Test
  void purchaseCommitTwiceThrowsIllegalStateException() {
    Purchase purchase = new Purchase(share, 1);
    purchase.commit(player);

    IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> purchase.commit(player));
    assertEquals("transaction has already been committed", exception.getMessage());
  }
}