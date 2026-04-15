package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.model.calculator.PurchaseCalculator;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

  private Player player;

  @BeforeEach
  void setUp() {
    player = new Player("Petter", new BigDecimal("10000"));
  }

  @Test
  void constructorValidInputSetsNameCorrectly() {
    assertEquals("Petter", player.getName());
  }

  @Test
  void constructorValidInputSetsMoneyToStartingMoney() {
    assertEquals(new BigDecimal("10000"), player.getMoney());
  }

  @Test
  void constructorValidInputCreatesEmptyPortfolio() {
    assertTrue(player.getPortfolio().getShares().isEmpty());
  }

  @Test
  void constructorValidInputCreatesEmptyTransactionArchive() {
    assertTrue(player.getTransactionArchive().isEmpty());
  }

  @Test
  void constructorNullNameThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player(null, new BigDecimal("10000")));
  }

  @Test
  void constructorBlankNameThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player(" ", new BigDecimal("10000")));
  }

  @Test
  void constructorNameTooLongThrowsIllegalArgumentException() {
    String longName = "A".repeat(51);
    assertThrows(IllegalArgumentException.class, () ->
        new Player(longName, new BigDecimal("10000")));
  }

  @Test
  void constructorNullStartingMoneyThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Petter", null));
  }

  @Test
  void constructorZeroStartingMoneyThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Petter", BigDecimal.ZERO));
  }

  @Test
  void constructorNegativeStartingMoneyThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Petter", new BigDecimal("-100")));
  }

  @Test
  void addMoneyValidAmountIncreasesBalance() {
    player.addMoney(new BigDecimal("500"));
    assertEquals(new BigDecimal("10500"), player.getMoney());
  }

  @Test
  void addMoneyNullAmountThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(null));
  }

  @Test
  void addMoneyZeroAmountThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(BigDecimal.ZERO));
  }

  @Test
  void addMoneyNegativeAmountThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(new BigDecimal("-100")));
  }

  @Test
  void withdrawMoneyValidAmountDecreasesBalance() {
    player.withdrawMoney(new BigDecimal("1000"));
    assertEquals(new BigDecimal("9000"), player.getMoney());
  }

  @Test
  void withdrawMoneyExactBalanceLeavesZero() {
    player.withdrawMoney(new BigDecimal("10000"));
    assertEquals(BigDecimal.ZERO, player.getMoney());
  }

  @Test
  void withdrawMoneyAmountExceedsBalanceThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(new BigDecimal("99999")));
  }

  @Test
  void withdrawMoneyNullAmountThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(null));
  }

  @Test
  void withdrawMoneyZeroAmountThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(BigDecimal.ZERO));
  }

  @Test
  void withdrawMoneyNegativeAmountThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(new BigDecimal("-100")));
  }

  @Test
  void getNetWorthWithEmptyPortfolioReturnsCashBalance() {
    assertEquals(new BigDecimal("10000"), player.getNetWorth());
  }

  @Test
  void getNetWorthIncludesPortfolioValue() {
    Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200"));
    Share share = new Share(stock, new BigDecimal("5"), new BigDecimal("200"));
    player.getPortfolio().addShare(share);

    assertEquals(new BigDecimal("10990.00"), player.getNetWorth());
  }

  @Test
  void getStatusReturnsNoviceByDefault() {
    assertEquals(PlayerRank.NOVICE, player.getStatus());
  }

  @Test
  void getStatusReturnsNoviceWhenNetWorthMetButNotWeeks() {
    player.addMoney(new BigDecimal("2000"));

    assertEquals(PlayerRank.NOVICE, player.getStatus());
  }

  @Test
  void getStatusReturnsNoviceWhenWeeksMetButNotNetWorth() {
    addDummyTransactionsForWeeks(10);

    assertEquals(PlayerRank.NOVICE, player.getStatus());
  }

  @Test
  void getStatusReturnsInvestorWhenRequirementsMet() {
    player.addMoney(new BigDecimal("2000"));
    addDummyTransactionsForWeeks(10);

    assertEquals(PlayerRank.INVESTOR, player.getStatus());
  }

  @Test
  void getStatusReturnsSpeculatorWhenRequirementsMet() {
    player.addMoney(new BigDecimal("10000"));
    addDummyTransactionsForWeeks(20);

    assertEquals(PlayerRank.SPECULATOR, player.getStatus());
  }

  private void addDummyTransactionsForWeeks(int amountOfWeeks) {
    Stock dummyStock = new Stock("DUMMY", "dummy", BigDecimal.ONE);
    Share dummyShare = new Share(dummyStock, BigDecimal.ONE, BigDecimal.ONE);
    for (int i = 1; i <= amountOfWeeks; i++) {
      Transaction dummyTransaction = new Transaction(dummyShare, i, new PurchaseCalculator(dummyShare)) {
        @Override
        protected void executeTransaction(Player player) {}
      };

      player.getTransactionArchive().add(dummyTransaction);
    }
  }
}
