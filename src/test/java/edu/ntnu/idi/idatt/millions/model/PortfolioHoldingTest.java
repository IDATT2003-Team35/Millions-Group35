package edu.ntnu.idi.idatt.millions.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioHoldingTest {

  private Stock equinor;
  private Share firstShare;
  private Share secondShare;

  @BeforeEach
  void setUp() {
    equinor = new Stock("EQNR", "Equinor", new BigDecimal("29.20"));
    firstShare = new Share(equinor, new BigDecimal("5"), new BigDecimal("29.20"));
    secondShare = new Share(equinor, new BigDecimal("3"), new BigDecimal("20.00"));
  }

  @Test
  void constructorNullSharesThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new PortfolioHolding(null));
  }

  @Test
  void constructorEmptySharesThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new PortfolioHolding(List.of()));
  }

  @Test
  void constructorSharesWithDifferentSymbolsThrowsIllegalArgumentException() {
    Stock tesla = new Stock("TSLA", "Tesla", new BigDecimal("23.20"));
    Share teslaShare = new Share(tesla, new BigDecimal("2"), new BigDecimal("23.20"));

    assertThrows(
        IllegalArgumentException.class,
        () -> new PortfolioHolding(List.of(firstShare, teslaShare)));
  }

  @Test
  void getSymbolReturnsStockSymbol() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals("EQNR", holding.getSymbol());
  }

  @Test
  void getCompanyReturnsCompanyName() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals("Equinor", holding.getCompany());
  }

  @Test
  void getSharesReturnsDefensiveCopy() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    List<Share> result = holding.getShares();
    result.clear();

    assertEquals(2, holding.getShares().size());
  }

  @Test
  void getQuantityReturnsTotalQuantity() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals(new BigDecimal("8"), holding.getQuantity());
  }

  @Test
  void getAveragePurchasePriceReturnsWeightedAverage() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals(new BigDecimal("25.75"), holding.getAveragePurchasePrice());
  }

  @Test
  void getCurrentPriceReturnsLatestStockPrice() {
    equinor.addNewSalesPrice(new BigDecimal("31.00"));
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals(new BigDecimal("31.00"), holding.getCurrentPrice());
  }

  @Test
  void getCurrentValueReturnsCurrentPriceTimesQuantity() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals(new BigDecimal("233.60"), holding.getCurrentValue());
  }

  @Test
  void getTotalGainLossUsesExistingCalculatorRules() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals(new BigDecimal("16.21"), holding.getTotalGainLoss());
  }

  @Test
  void getTotalGainLossPercentUsesGroupedTotals() {
    PortfolioHolding holding = new PortfolioHolding(List.of(firstShare, secondShare));

    assertEquals(new BigDecimal("7.8300"), holding.getTotalGainLossPercent());
  }
}
