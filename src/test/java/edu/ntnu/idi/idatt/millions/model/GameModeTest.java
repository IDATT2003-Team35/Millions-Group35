package edu.ntnu.idi.idatt.millions.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameModeTest {

  @Test
  void sandboxHasNoWeekLimit() {
    assertNull(GameMode.SANDBOX.getWeekLimit());
    assertFalse(GameMode.SANDBOX.hasWeekLimit());
  }

  @Test
  void challengeHasFiftyTwoWeekLimit() {
    assertEquals(Integer.valueOf(52), GameMode.CHALLENGE.getWeekLimit());
    assertTrue(GameMode.CHALLENGE.hasWeekLimit());
  }

  @Test
  void valueOfReturnsMatchingEnumForKnownName() {
    assertSame(GameMode.SANDBOX, GameMode.valueOf("SANDBOX"));
    assertSame(GameMode.CHALLENGE, GameMode.valueOf("CHALLENGE"));
  }

  @Test
  void valueOfThrowsForUnknownName() {
    assertThrows(IllegalArgumentException.class, () -> GameMode.valueOf("TOURNAMENT"));
  }
}
