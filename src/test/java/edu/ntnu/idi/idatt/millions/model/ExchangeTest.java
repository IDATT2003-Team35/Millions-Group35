package edu.ntnu.idi.idatt.millions.model;

import edu.ntnu.idi.idatt.millions.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.transaction.Sale;
import edu.ntnu.idi.idatt.millions.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeTest {
  private Exchange exchange;
  private Player player;
  private Stock equinor;
  private Stock tesla;
  private Stock apple;

  @BeforeEach
  void setUp() {
    equinor = new Stock("EQNR", "Equinor", new BigDecimal("29.2"));
    tesla = new Stock("TSLA", "Tesla", new BigDecimal("23.2"));
    apple = new Stock("AAPL", "Apple", new BigDecimal("150.0"));
    List<Stock> stocks = List.of(tesla, equinor, apple);
    exchange = new Exchange("OSEBX", stocks);
    player = new Player("Petter", new BigDecimal("10000"));
  }

  @Test
  void buyValidInputUpdatesPlayerStateAndReturnsCommittedPurchase() {
    Transaction result = exchange.buy("EQNR", new BigDecimal("5"), player);
    assertInstanceOf(Purchase.class, result);
    assertTrue(result.isCommitted());
    assertEquals(exchange.getWeek(), result.getWeek());
    assertEquals(1, player.getPortfolio().getShares("EQNR").size());
    assertEquals(1, player.getTransactionArchive().getPurchases(1).size());
  }

  @Test
  void buyInsufficientFundsThrowsIllegalStateException() {
    Player poorPlayer = new Player("Broke", new BigDecimal("1"));
    assertThrows(IllegalStateException.class, () ->
        exchange.buy("EQNR", new BigDecimal("1"), poorPlayer));
  }

  @Test
  void buyInvalidSymbolThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        exchange.buy("INVALID", new BigDecimal("5"), player));
  }

  @Test
  void buyZeroQuantityThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        exchange.buy("EQNR", BigDecimal.ZERO, player));
  }

  @Test
  void buyNegativeQuantityThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        exchange.buy("EQNR", new BigDecimal("-1"), player));
  }

  @Test
  void buyNullQuantityThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        exchange.buy("EQNR", null, player));
  }

  @Test
  void buyNullPlayerThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        exchange.buy("EQNR", BigDecimal.ONE, null));
  }

  @Test
  void hasStockExistingSymbolReturnsTrue() {
    assertTrue(exchange.hasStock("EQNR"));
  }

  @Test
  void hasStockMissingSymbolReturnsFalse() {
    assertFalse(exchange.hasStock("MSFT"));
  }

  @Test
  void getStockExistingSymbolReturnsStock() {
    assertSame(equinor, exchange.getStock("EQNR"));
  }

  @Test
  void findStocksCaseInsensitiveMatchesSymbolAndCompany() {
    List<Stock> bySymbol = exchange.findStocks("tsl");
    List<Stock> byCompany = exchange.findStocks("EQUIN");

    assertEquals(1, bySymbol.size());
    assertSame(tesla, bySymbol.get(0));
    assertEquals(1, byCompany.size());
    assertSame(equinor, byCompany.get(0));
  }

  @Test
  void findStocksBlankAndNullThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () ->
        exchange.findStocks(" "));
    assertThrows(IllegalArgumentException.class, () ->
        exchange.findStocks(null));
  }

  @Test
  void sellValidShareUpdatesPlayerStateAndReturnsCommittedSale() {
    exchange.buy("EQNR", new BigDecimal("5"), player);
    Share share = player.getPortfolio().getShares("EQNR").getFirst();

    Transaction result = exchange.sell(share, player);

    assertInstanceOf(Sale.class, result);
    assertTrue(result.isCommitted());
    assertEquals(exchange.getWeek(), result.getWeek());
    assertEquals(0, player.getPortfolio().getShares("EQNR").size());
    assertEquals(1, player.getTransactionArchive().getSales(1).size());
  }


  @Test
  void sellNullShareThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.sell(null, player));
  }

  @Test
  void sellNullPlayerThrowsIllegalArgumentException() {
    Share share = new Share(equinor, BigDecimal.ONE, equinor.getSalesPrice());
    assertThrows(IllegalArgumentException.class, () -> exchange.sell(share, null));
  }

  @Test
  void advanceWeekKeepsPricesPositive() {
    int weekBefore = exchange.getWeek();
    exchange.advance();

    assertEquals(weekBefore + 1, exchange.getWeek());
    assertTrue(equinor.getSalesPrice().compareTo(new BigDecimal("0.01")) >= 0);
    assertTrue(tesla.getSalesPrice().compareTo(new BigDecimal("0.01")) >= 0);
  }

  @Test
  void getGainersReturnsSortedPositiveChanges() {
    equinor.addNewSalesPrice(new BigDecimal("34.2")); // +5.0
    apple.addNewSalesPrice(new BigDecimal("160.0")); // +10.0
    tesla.addNewSalesPrice(new BigDecimal("21.2")); // -2.0

    List<Stock> gainers = exchange.getGainers(10);

    assertEquals(2, gainers.size());
    assertSame(apple, gainers.get(0));
    assertSame(equinor, gainers.get(1));
  }

  @Test
  void getLosersReturnsSortedNegativeChanges() {
    equinor.addNewSalesPrice(new BigDecimal("27.2")); // -2.0
    apple.addNewSalesPrice(new BigDecimal("140.0")); // -10.0
    tesla.addNewSalesPrice(new BigDecimal("25.2")); // +2.0

    List<Stock> losers = exchange.getLosers(10);

    assertEquals(2, losers.size());
    assertSame(apple, losers.get(0));
    assertSame(equinor, losers.get(1));
  }

  @Test
  void getGainersAppliesLimitCorrectly() {
    equinor.addNewSalesPrice(new BigDecimal("34.2")); // +5.0
    apple.addNewSalesPrice(new BigDecimal("160.0")); // +10.0

    List<Stock> gainers = exchange.getGainers(1);

    assertEquals(1, gainers.size());
    assertSame(apple, gainers.get(0));
  }

  @Test
  void getLosersAppliesLimitCorrectly() {
    equinor.addNewSalesPrice(new BigDecimal("27.2")); // -2.0
    apple.addNewSalesPrice(new BigDecimal("140.0")); // -10.0

    List<Stock> losers = exchange.getLosers(1);

    assertEquals(1, losers.size());
    assertSame(apple, losers.get(0));
  }

  @Test
  void getGainersInvalidLimitThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(0));
    assertThrows(IllegalArgumentException.class, () -> exchange.getGainers(-5));
  }

  @Test
  void getLosersInvalidLimitThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(0));
    assertThrows(IllegalArgumentException.class, () -> exchange.getLosers(-5));
  }
}
