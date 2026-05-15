package edu.ntnu.idi.idatt.millions.file.save;

import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSaveServiceTest {
  @TempDir
  Path tempDir;

  private GameSaveService service;

  @BeforeEach
  void setUp() throws GameSaveException {
    service = new GameSaveService(tempDir);
  }

  @Test
  void saveCreatesJsonFile() throws Exception {
    GameSession session = createSession();

    Path savePath = service.save(session);

    assertTrue(Files.exists(savePath));
    assertTrue(savePath.toString().endsWith(".json"));
  }

  @Test
  void saveNullSessionThrowsGameSaveException() {
    assertThrows(GameSaveException.class, () -> service.save(null));
  }

  @Test
  void loadNullPathThrowsGameSaveException() {
    assertThrows(GameSaveException.class, () -> service.load(null));
  }

  @Test
  void loadMissingFileThrowsGameSaveException() {
    Path missingPath = tempDir.resolve("missing.json");

    assertThrows(GameSaveException.class, () -> service.load(missingPath));
  }

  @Test
  void loadBrokenJsonThrowsGameSaveException() throws Exception {
    Path brokenSave = tempDir.resolve("broken.json");
    Files.writeString(brokenSave, "{ not valid json");

    assertThrows(GameSaveException.class, () -> service.load(brokenSave));
  }

  @Test
  void listSaveFilesEmptyFolderReturnsEmptyList() throws Exception {
    assertTrue(service.listSaveFiles().isEmpty());
  }

  @Test
  void listSaveFilesMissingFolderReturnsEmptyList() throws Exception {
    GameSaveService missingFolderService = new GameSaveService(tempDir.resolve("missing"));

    assertTrue(missingFolderService.listSaveFiles().isEmpty());
  }

  @Test
  void listSaveFilesReturnsOnlyJsonFilesSortedByName() throws Exception {
    Path secondSave = tempDir.resolve("second.json");
    Path firstSave = tempDir.resolve("first.json");
    Path textFile = tempDir.resolve("notes.txt");
    Files.writeString(secondSave, "{}");
    Files.writeString(firstSave, "{}");
    Files.writeString(textFile, "not a save");

    List<Path> saveFiles = service.listSaveFiles();

    assertEquals(List.of(firstSave, secondSave), saveFiles);
  }

  @Test
  void saveAndLoadRestoresPlayerData() throws Exception {
    GameSession original = createSessionWithActivity();

    GameSession loaded = service.load(service.save(original));

    assertAll(
        () -> assertEquals(original.getPlayer().getName(), loaded.getPlayer().getName()),
        () -> assertEquals(original.getPlayer().getStartingMoney(),
            loaded.getPlayer().getStartingMoney()),
        () -> assertEquals(original.getPlayer().getMoney(), loaded.getPlayer().getMoney())
    );
  }

  @Test
  void saveAndLoadRestoresExchangeAndStocks() throws Exception {
    GameSession original = createSessionWithActivity();

    GameSession loaded = service.load(service.save(original));

    assertEquals(original.getExchange().getName(), loaded.getExchange().getName());
    assertEquals(original.getExchange().getWeek(), loaded.getExchange().getWeek());
    assertEquals(original.getExchange().getStocks().size(), loaded.getExchange().getStocks().size());

    for (Stock originalStock : original.getExchange().getStocks()) {
      Stock loadedStock = loaded.getExchange().getStock(originalStock.getSymbol());
      assertEquals(originalStock.getCompany(), loadedStock.getCompany());
      assertEquals(originalStock.getHistoricalPrices(), loadedStock.getHistoricalPrices());
    }
  }

  @Test
  void saveAndLoadRestoresPortfolioShares() throws Exception {
    GameSession original = createSessionWithActivity();

    GameSession loaded = service.load(service.save(original));

    List<Share> originalShares = original.getPlayer().getPortfolio().getShares();
    List<Share> loadedShares = loaded.getPlayer().getPortfolio().getShares();

    assertEquals(originalShares.size(), loadedShares.size());
    for (Share originalShare : originalShares) {
      Share loadedShare = loaded.getPlayer().getPortfolio()
          .getShares(originalShare.getStock().getSymbol()).getFirst();

      assertEquals(originalShare.getStock().getSymbol(), loadedShare.getStock().getSymbol());
      assertEquals(originalShare.getQuantity(), loadedShare.getQuantity());
      assertEquals(originalShare.getPurchasePrice(), loadedShare.getPurchasePrice());
    }
  }

  @Test
  void saveAndLoadRestoresTransactionArchive() throws Exception {
    GameSession original = createSessionWithActivity();

    GameSession loaded = service.load(service.save(original));

    List<Transaction> originalTransactions = original.getPlayer().getTransactionArchive().getAll();
    List<Transaction> loadedTransactions = loaded.getPlayer().getTransactionArchive().getAll();

    assertEquals(originalTransactions.size(), loadedTransactions.size());
    assertInstanceOf(Purchase.class, loadedTransactions.get(0));
    assertInstanceOf(Purchase.class, loadedTransactions.get(1));
    assertInstanceOf(Sale.class, loadedTransactions.get(2));

    for (int i = 0; i < originalTransactions.size(); i++) {
      Transaction originalTransaction = originalTransactions.get(i);
      Transaction loadedTransaction = loadedTransactions.get(i);

      assertEquals(originalTransaction.getWeek(), loadedTransaction.getWeek());
      assertEquals(originalTransaction.isCommitted(), loadedTransaction.isCommitted());
      assertEquals(originalTransaction.getShare().getStock().getSymbol(),
          loadedTransaction.getShare().getStock().getSymbol());
      assertEquals(originalTransaction.getShare().getQuantity(),
          loadedTransaction.getShare().getQuantity());
      assertEquals(originalTransaction.getShare().getPurchasePrice(),
          loadedTransaction.getShare().getPurchasePrice());
    }
  }

  @Test
  void saveAndLoadRestoresNetWorthHistory() throws Exception {
    GameSession original = createSessionWithActivity();

    GameSession loaded = service.load(service.save(original));

    assertEquals(original.getNetWorthHistory(), loaded.getNetWorthHistory());
  }

  private GameSession createSession() {
    Stock apple = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));
    Stock tesla = new Stock("TSLA", "Tesla Inc.", new BigDecimal("100.00"));
    Stock microsoft = new Stock("MSFT", "Microsoft", new BigDecimal("300.00"));
    Exchange exchange = new Exchange("NASDAQ", List.of(apple, tesla, microsoft));
    Player player = new Player("Petter", new BigDecimal("10000.00"));
    return new GameSession(player, exchange);
  }

  private GameSession createSessionWithActivity() {
    GameSession session = createSession();

    session.buyStock("AAPL", new BigDecimal("2"));
    session.buyStock("TSLA", new BigDecimal("3"));
    Share appleShare = session.getPlayer().getPortfolio().getShares("AAPL").getFirst();
    session.sellShare(appleShare);
    session.advanceWeek();

    return session;
  }
}
