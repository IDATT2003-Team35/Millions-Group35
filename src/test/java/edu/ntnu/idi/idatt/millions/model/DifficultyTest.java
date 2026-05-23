package edu.ntnu.idi.idatt.millions.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DifficultyTest {

  @Test
  void easyHasCorrectStartingCapitalAndVolatility() {
    assertEquals(new BigDecimal("10000"), Difficulty.EASY.getDefaultStartingCapital());
    assertEquals(0.05, Difficulty.EASY.getVolatility());
  }

  @Test
  void normalHasCorrectStartingCapitalAndVolatility() {
    assertEquals(new BigDecimal("5000"), Difficulty.NORMAL.getDefaultStartingCapital());
    assertEquals(0.10, Difficulty.NORMAL.getVolatility());
  }

  @Test
  void hardHasCorrectStartingCapitalAndVolatility() {
    assertEquals(new BigDecimal("1000"), Difficulty.HARD.getDefaultStartingCapital());
    assertEquals(0.20, Difficulty.HARD.getVolatility());
  }

  @Test
  void volatilityIncreasesWithDifficulty() {
    assertEquals(0.05, Difficulty.EASY.getVolatility());
    assertEquals(0.10, Difficulty.NORMAL.getVolatility());
    assertEquals(0.20, Difficulty.HARD.getVolatility());
  }

  @Test
  void startingCapitalDecreasesWithDifficulty() {
    assertEquals(0, new BigDecimal("10000").compareTo(Difficulty.EASY.getDefaultStartingCapital()));
    assertEquals(0, new BigDecimal("5000").compareTo(Difficulty.NORMAL.getDefaultStartingCapital()));
    assertEquals(0, new BigDecimal("1000").compareTo(Difficulty.HARD.getDefaultStartingCapital()));
  }

  @Test
  void valueOfReturnsMatchingEnumForKnownName() {
    assertSame(Difficulty.NORMAL, Difficulty.valueOf("NORMAL"));
    assertSame(Difficulty.EASY, Difficulty.valueOf("EASY"));
    assertSame(Difficulty.HARD, Difficulty.valueOf("HARD"));
  }

  @Test
  void valueOfThrowsForUnknownName() {
    assertThrows(IllegalArgumentException.class, () -> Difficulty.valueOf("INSANE"));
  }
}
