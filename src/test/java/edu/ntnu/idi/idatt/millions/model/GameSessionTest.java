package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {
  private Exchange exchange;
  private Player player;
  private GameSession session;
  private CountingObserver observer;

  @BeforeEach
  void setUp() {
    Stock equinor = new Stock("EQNR", "Equinor", new BigDecimal("29.2"));
    Stock tesla = new Stock("TSLA", "Tesla", new BigDecimal("23.2"));
    Stock apple = new Stock("AAPL", "Apple", new BigDecimal("150.0"));
    exchange = new Exchange("OSEBX", List.of(equinor, tesla, apple));
    player = new Player("Petter", new BigDecimal("10000"));
    session = new GameSession(player, exchange);
    observer = new CountingObserver();
  }

  @Test
  void constructorValidInputsCreatesSession() {
    assertSame(player, session.getPlayer());
    assertSame(exchange, session.getExchange());
  }

  @Test
  void nullInputsThrowIllegalArgumentException() {
    assertAll(
        () -> assertThrows(IllegalArgumentException.class,
            () -> new GameSession(null, exchange)),
        () -> assertThrows(IllegalArgumentException.class,
            () -> new GameSession(player, null)),
        () -> assertThrows(IllegalArgumentException.class,
            () -> session.addObserver(null))
    );
  }

  @Test
  void addObserverThenNotifyInvokesUpdate() {
    session.addObserver(observer);
    session.notifyObservers();
    assertEquals(1, observer.updateCount);
  }

  @Test
  void addObserverMultipleAllReceiveNotification() {
    CountingObserver second = new CountingObserver();
    CountingObserver third = new CountingObserver();
    session.addObserver(observer);
    session.addObserver(second);
    session.addObserver(third);

    session.notifyObservers();

    assertEquals(1, observer.updateCount);
    assertEquals(1, second.updateCount);
    assertEquals(1, third.updateCount);
  }

  @Test
  void removeObserverStopsReceivingNotifications() {
    session.addObserver(observer);
    session.removeObserver(observer);

    session.notifyObservers();

    assertEquals(0, observer.updateCount);
  }

  @Test
  void buyStockValidInputNotifiesObservers() {
    session.addObserver(observer);

    session.buyStock("EQNR", new BigDecimal("5"));

    assertEquals(1, observer.updateCount);
    assertEquals(1, player.getPortfolio().getShares("EQNR").size());
    assertEquals(1, player.getTransactionArchive().getPurchases(1).size());
  }

  @Test
  void buyStockInsufficientFundsDoesNotNotifyObservers() {
    Player poorPlayer = new Player("Broke", new BigDecimal("1"));
    GameSession poorSession = new GameSession(poorPlayer, exchange);
    poorSession.addObserver(observer);

    assertThrows(IllegalStateException.class, () ->
        poorSession.buyStock("EQNR", new BigDecimal("5")));
    assertEquals(0, observer.updateCount);
  }

  @Test
  void sellShareValidInputNotifiesObservers() {
    session.addObserver(observer);
    session.buyStock("EQNR", new BigDecimal("5"));

    Share share = player.getPortfolio().getShares("EQNR").getFirst();
    session.sellShare(share);

    assertEquals(2, observer.updateCount);
    assertEquals(0, player.getPortfolio().getShares("EQNR").size());
  }

  @Test
  void sellShareNotOwnedDoesNotNotifyObservers() {
    session.addObserver(observer);
    Stock equinor = exchange.getStock("EQNR");
    Share unownedShare = new Share(equinor, new BigDecimal("5"), new BigDecimal("29.2"));

    assertThrows(IllegalStateException.class, () -> session.sellShare(unownedShare));
    assertEquals(0, observer.updateCount);
  }

  @Test
  void sellStockValidInputNotifiesObserversOnce() {
    session.addObserver(observer);
    session.buyStock("EQNR", new BigDecimal("5"));

    session.sellStock("EQNR", new BigDecimal("2"));

    assertEquals(2, observer.updateCount);
    assertEquals(new BigDecimal("3"),
        player.getPortfolio().getShares("EQNR").getFirst().getQuantity());
    assertEquals(1, player.getTransactionArchive().getSales(1).size());
  }

  @Test
  void sellStockInvalidQuantityDoesNotNotifyObservers() {
    session.addObserver(observer);
    session.buyStock("EQNR", new BigDecimal("5"));

    assertThrows(IllegalArgumentException.class, () ->
        session.sellStock("EQNR", new BigDecimal("6")));

    assertEquals(1, observer.updateCount);
    assertEquals(new BigDecimal("5"),
        player.getPortfolio().getShares("EQNR").getFirst().getQuantity());
  }

  @Test
  void advanceWeekIncrementsWeekAndNotifiesObservers() {
    session.addObserver(observer);

    session.advanceWeek();

    assertEquals(2, exchange.getWeek());
    assertEquals(1, observer.updateCount);
  }

  @Test
  void defaultConstructorUsesNormalDifficultyAndSandboxMode() {
    assertSame(Difficulty.NORMAL, session.getDifficulty());
    assertSame(GameMode.SANDBOX, session.getMode());
  }

  @Test
  void constructorWithDifficultyAndModeStoresValues() {
    GameSession challengeSession = new GameSession(
        player, exchange, Difficulty.HARD, GameMode.CHALLENGE);
    assertSame(Difficulty.HARD, challengeSession.getDifficulty());
    assertSame(GameMode.CHALLENGE, challengeSession.getMode());
  }

  @Test
  void constructorWithNullDifficultyThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> new GameSession(player, exchange, null, GameMode.SANDBOX));
  }

  @Test
  void constructorWithNullModeThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class,
        () -> new GameSession(player, exchange, Difficulty.NORMAL, null));
  }

  @Test
  void isGameOverIsFalseForSandboxRegardlessOfWeek() {
    for (int i = 0; i < 60; i++) {
      exchange.advance();
    }
    assertFalse(session.isGameOver());
  }

  @Test
  void isGameOverIsFalseForChallengeBeforeWeekLimit() {
    Exchange challengeExchange = new Exchange("OSEBX",
        List.of(new Stock("EQNR", "Equinor", new BigDecimal("29.2"))));
    GameSession challengeSession = new GameSession(
        player, challengeExchange, Difficulty.NORMAL, GameMode.CHALLENGE);
    for (int i = 0; i < 50; i++) {
      challengeSession.advanceWeek();
    }
    assertFalse(challengeSession.isGameOver());
  }

  @Test
  void isGameOverIsTrueForChallengeAtWeekLimit() {
    Exchange challengeExchange = new Exchange("OSEBX",
        List.of(new Stock("EQNR", "Equinor", new BigDecimal("29.2"))),
        52);
    GameSession challengeSession = new GameSession(
        player, challengeExchange, Difficulty.NORMAL, GameMode.CHALLENGE);
    assertTrue(challengeSession.isGameOver());
  }

  @Test
  void advanceWeekThrowsWhenGameIsOver() {
    Exchange challengeExchange = new Exchange("OSEBX",
        List.of(new Stock("EQNR", "Equinor", new BigDecimal("29.2"))),
        52);
    GameSession challengeSession = new GameSession(
        player, challengeExchange, Difficulty.NORMAL, GameMode.CHALLENGE);
    assertThrows(IllegalStateException.class, challengeSession::advanceWeek);
  }

  private static class CountingObserver implements Observer {
    int updateCount = 0;

    @Override
    public void update() {
      updateCount++;
    }
  }
}
