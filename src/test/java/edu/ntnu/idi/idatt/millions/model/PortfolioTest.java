package edu.ntnu.idi.idatt.millions.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {

  private Portfolio portfolio;
  private Stock equinor;
  private Share share;

  @BeforeEach
  void setUp() {
    equinor = new Stock("EQNR", "Equinor", new BigDecimal("29.20"));
    share = new Share(equinor, new BigDecimal("5"), new BigDecimal("29.20"));
    portfolio = new Portfolio();
  }

  @Test
  void addShare_validShare_returnsTrue() {
    boolean result = portfolio.addShare(share);
    assertTrue(result);
  }

  @Test
  void addShare_validShare_shareIsInPortfolio() {
    portfolio.addShare(share);
    assertTrue(portfolio.contains(share));
  }

  @Test
  void addShare_nullShare_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.addShare(null));
  }

  @Test
  void removeShare_existingShare_returnsTrue() {
    portfolio.addShare(share);
    boolean result = portfolio.removeShare(share);
    assertTrue(result);
  }

  @Test
  void removeShare_existingShare_shareIsNoLongerInPortfolio() {
    portfolio.addShare(share);
    portfolio.removeShare(share);
    assertFalse(portfolio.contains(share));
  }

  @Test
  void removeShare_shareNotInPortfolio_returnsFalse() {
    boolean result = portfolio.removeShare(share);
    assertFalse(result);
  }

  @Test
  void removeShare_nullShare_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.removeShare(null));
  }

  @Test
  void getShares_returnsAllShares() {
    Share share2 = new Share(equinor, new BigDecimal("3"), new BigDecimal("29.20"));
    portfolio.addShare(share);
    portfolio.addShare(share2);

    List<Share> result = portfolio.getShares();

    assertEquals(2, result.size());
  }

  @Test
  void getShares_bySymbol_returnsMatchingShares() {
    Stock tesla = new Stock("TSLA", "Tesla", new BigDecimal("23.20"));
    Share teslaShare = new Share(tesla, new BigDecimal("2"), new BigDecimal("23.20"));
    portfolio.addShare(share);
    portfolio.addShare(teslaShare);

    List<Share> result = portfolio.getShares("EQNR");

    assertEquals(1, result.size());
  }

  @Test
  void getShares_bySymbol_nullSymbol_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.getShares(null));
  }

  @Test
  void getShares_bySymbol_blankSymbol_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.getShares(" "));
  }

  @Test
  void contains_shareNotInPortfolio_returnsFalse() {
    assertFalse(portfolio.contains(share));
  }

  @Test
  void contains_nullShare_throwsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.contains(null));
  }
}
