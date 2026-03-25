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
  void addShareValidShareReturnsTrue() {
    boolean result = portfolio.addShare(share);
    assertTrue(result);
  }

  @Test
  void addShareValidShareShareIsInPortfolio() {
    portfolio.addShare(share);
    assertTrue(portfolio.contains(share));
  }

  @Test
  void addShareNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.addShare(null));
  }

  @Test
  void removeShareExistingShareReturnsTrue() {
    portfolio.addShare(share);
    boolean result = portfolio.removeShare(share);
    assertTrue(result);
  }

  @Test
  void removeShareExistingShareShareIsNoLongerInPortfolio() {
    portfolio.addShare(share);
    portfolio.removeShare(share);
    assertFalse(portfolio.contains(share));
  }

  @Test
  void removeShareShareNotInPortfolioReturnsFalse() {
    boolean result = portfolio.removeShare(share);
    assertFalse(result);
  }

  @Test
  void removeShareNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.removeShare(null));
  }

  @Test
  void getSharesReturnsAllShares() {
    Share share2 = new Share(equinor, new BigDecimal("3"), new BigDecimal("29.20"));
    portfolio.addShare(share);
    portfolio.addShare(share2);

    List<Share> result = portfolio.getShares();

    assertEquals(2, result.size());
  }

  @Test
  void getSharesBySymbolReturnsMatchingShares() {
    Stock tesla = new Stock("TSLA", "Tesla", new BigDecimal("23.20"));
    Share teslaShare = new Share(tesla, new BigDecimal("2"), new BigDecimal("23.20"));
    portfolio.addShare(share);
    portfolio.addShare(teslaShare);

    List<Share> result = portfolio.getShares("EQNR");

    assertEquals(1, result.size());
  }

  @Test
  void getSharesBySymbolNullSymbolThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.getShares(null));
  }

  @Test
  void getSharesBySymbolBlankSymbolThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.getShares(" "));
  }

  @Test
  void containsShareNotInPortfolioReturnsFalse() {
    assertFalse(portfolio.contains(share));
  }

  @Test
  void containsNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        portfolio.contains(null));
  }

  @Test
  void checkIfGetNetworhtMethodReturnsCorrectAmount(){
    portfolio.addShare(share);
    assertEquals(new BigDecimal("146.00"), portfolio.getNetWorth());
  }
}
