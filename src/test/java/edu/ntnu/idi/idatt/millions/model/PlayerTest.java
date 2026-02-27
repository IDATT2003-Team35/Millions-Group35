package edu.ntnu.idi.idatt.millions.model;

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
  void constructor_validInput_setsNameCorrectly() {
    assertEquals("Petter", player.getName());
  }

  @Test
  void constructor_validInput_setsMoneyToStartingMoney() {
    assertEquals(new BigDecimal("10000"), player.getMoney());
  }

  @Test
  void constructor_validInput_createsEmptyPortfolio() {
    assertTrue(player.getPortfolio().getShares().isEmpty());
  }

  @Test
  void constructor_validInput_createsEmptyTransactionArchive() {
    assertTrue(player.getTransactionArchive().isEmpty());
  }

  @Test
  void constructor_nullName_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player(null, new BigDecimal("10000")));
  }

  @Test
  void constructor_blankName_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player(" ", new BigDecimal("10000")));
  }

  @Test
  void constructor_nameTooLong_throwsIllegalArgumentException() {
    String longName = "A".repeat(51);
    assertThrows(IllegalArgumentException.class, () ->
        new Player(longName, new BigDecimal("10000")));
  }

  @Test
  void constructor_nullStartingMoney_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Petter", null));
  }

  @Test
  void constructor_zeroStartingMoney_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Petter", BigDecimal.ZERO));
  }

  @Test
  void constructor_negativeStartingMoney_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        new Player("Petter", new BigDecimal("-100")));
  }

  @Test
  void addMoney_validAmount_increasesBalance() {
    player.addMoney(new BigDecimal("500"));
    assertEquals(new BigDecimal("10500"), player.getMoney());
  }

  @Test
  void addMoney_nullAmount_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(null));
  }

  @Test
  void addMoney_zeroAmount_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(BigDecimal.ZERO));
  }

  @Test
  void addMoney_negativeAmount_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.addMoney(new BigDecimal("-100")));
  }

  @Test
  void withdrawMoney_validAmount_decreasesBalance() {
    player.withdrawMoney(new BigDecimal("1000"));
    assertEquals(new BigDecimal("9000"), player.getMoney());
  }

  @Test
  void withdrawMoney_exactBalance_leavesZero() {
    player.withdrawMoney(new BigDecimal("10000"));
    assertEquals(BigDecimal.ZERO, player.getMoney());
  }

  @Test
  void withdrawMoney_amountExceedsBalance_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(new BigDecimal("99999")));
  }

  @Test
  void withdrawMoney_nullAmount_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(null));
  }

  @Test
  void withdrawMoney_zeroAmount_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(BigDecimal.ZERO));
  }

  @Test
  void withdrawMoney_negativeAmount_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        player.withdrawMoney(new BigDecimal("-100")));
  }
}
