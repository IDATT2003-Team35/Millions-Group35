package edu.ntnu.idi.idatt.millions.file.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idi.idatt.millions.factory.TransactionFactory;
import edu.ntnu.idi.idatt.millions.file.save.dto.ExchangeSaveData;
import edu.ntnu.idi.idatt.millions.file.save.dto.GameSaveData;
import edu.ntnu.idi.idatt.millions.file.save.dto.PlayerSaveData;
import edu.ntnu.idi.idatt.millions.file.save.dto.ShareSaveData;
import edu.ntnu.idi.idatt.millions.file.save.dto.StockSaveData;
import edu.ntnu.idi.idatt.millions.file.save.dto.TransactionSaveData;
import edu.ntnu.idi.idatt.millions.model.Difficulty;
import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.GameMode;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service responsible for saving and loading game sessions as JSON files.
 *
 * <p>The service converts between the game model and save DTOs before using Jackson to read and
 * write JSON.
 */
public class GameSaveService {
  private static final Path DEFAULT_SAVE_FOLDER = Path.of("saves");
  private final ObjectMapper objectMapper;
  private final Path saveFolder;

  /**
   * Creates a save service using the default save folder.
   *
   * @throws GameSaveException if the default save folder is invalid
   */
  public GameSaveService() throws GameSaveException {
    this(DEFAULT_SAVE_FOLDER);
  }

  /**
   * Creates a save service using the given save folder.
   *
   * @param saveFolder the folder where save files are written
   * @throws GameSaveException if saveFolder is null
   */
  public GameSaveService(Path saveFolder) throws GameSaveException {
    if (saveFolder == null) {
      throw new GameSaveException("Save folder cannot be null");
    }
    this.objectMapper = new ObjectMapper();
    this.saveFolder = saveFolder;
  }

  /**
   * Saves the given game session to a JSON file.
   *
   * @param session the game session to save
   * @return the path to the written save file
   * @throws GameSaveException if the session is null or the file cannot be written
   */
  public Path save(GameSession session) throws GameSaveException {
    if (session == null) {
      throw new GameSaveException("Session cannot be null");
    }

    createSaveFolderIfMissing();
    GameSaveData saveData = createSaveData(session);
    Path savePath = createSavePath(session);
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(savePath.toFile(), saveData);
      return savePath;
    } catch (IOException e) {
      throw new GameSaveException("Could not save game to: " + savePath, e);
    }
  }

  /**
   * Loads a game session from a JSON save file.
   *
   * @param path the path to the save file
   * @return the restored game session
   * @throws GameSaveException if the path is invalid or the save file cannot be read/restored
   */
  public GameSession load(Path path) throws GameSaveException {
    if (path == null) {
      throw new GameSaveException("Save file path cannot be null");
    }

    if (!Files.exists(path)) {
      throw new GameSaveException("Save file does not exist");
    }

    try {
      GameSaveData gameData = objectMapper.readValue(path.toFile(), GameSaveData.class);
      return createGameSession(gameData);
    } catch (IOException e) {
      throw new GameSaveException("Could not read save file: " + path, e);
    }
  }

  /**
   * Lists available JSON save files in the configured save folder.
   *
   * @return a sorted list of save file paths
   * @throws GameSaveException if the save folder cannot be read
   */
  public List<Path> listSaveFiles() throws GameSaveException {
    if (!Files.exists(saveFolder)) {
      return List.of();
    }
    if (!Files.isDirectory(saveFolder)) {
      throw new GameSaveException("Save folder is not a directory: " + saveFolder);
    }

    try (var paths = Files.list(saveFolder)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".json"))
          .sorted(Comparator.comparing(path -> path.getFileName().toString()))
          .toList();
    } catch (IOException e) {
      throw new GameSaveException("Could not list save files in: " + saveFolder, e);
    }
  }

  private void createSaveFolderIfMissing() throws GameSaveException {
    try {
      Files.createDirectories(saveFolder);
    } catch (IOException e) {
      throw new GameSaveException("Could not create save folder: " + saveFolder, e);
    }
  }

  private GameSaveData createSaveData(GameSession session) {
    Player player = session.getPlayer();
    Exchange exchange = session.getExchange();

    PlayerSaveData playerData =
        new PlayerSaveData(
            player.getName(),
            player.getStartingMoney().toPlainString(),
            player.getMoney().toPlainString());

    ExchangeSaveData exchangeData = new ExchangeSaveData(exchange.getName(), exchange.getWeek());

    List<StockSaveData> stockData =
        session.getExchange().getStocks().stream().map(this::createStockSaveData).toList();

    List<ShareSaveData> shareData =
        session.getPlayer().getPortfolio().getShares().stream()
            .map(this::createShareSaveData)
            .toList();

    List<TransactionSaveData> transactionData =
        session.getPlayer().getTransactionArchive().getAll().stream()
            .map(this::createTransactionSaveData)
            .toList();

    List<String> netWorthHistoryData =
        session.getNetWorthHistory().stream().map(BigDecimal::toPlainString).toList();

    return new GameSaveData(
        playerData,
        exchangeData,
        stockData,
        shareData,
        transactionData,
        netWorthHistoryData,
        session.getDifficulty().name(),
        session.getMode().name());
  }

  private StockSaveData createStockSaveData(Stock stock) {
    return new StockSaveData(
        stock.getSymbol(),
        stock.getCompany(),
        stock.getHistoricalPrices().stream().map(BigDecimal::toPlainString).toList());
  }

  private ShareSaveData createShareSaveData(Share share) {
    return new ShareSaveData(
        share.getStock().getSymbol(),
        share.getQuantity().toPlainString(),
        share.getPurchasePrice().toPlainString());
  }

  private TransactionSaveData createTransactionSaveData(Transaction transaction) {
    return new TransactionSaveData(
        transaction.getClass().getSimpleName(),
        transaction.getShare().getStock().getSymbol(),
        transaction.getShare().getQuantity().toPlainString(),
        transaction.getShare().getPurchasePrice().toPlainString(),
        transaction.getWeek(),
        transaction.isCommitted());
  }

  private Path createSavePath(GameSession session) {
    String mode = session.getMode().toString().toLowerCase();
    String difficulty = session.getDifficulty().toString().toLowerCase();
    String playerName = session.getPlayer().getName()
            .trim()
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

    if (playerName.isBlank()) {
      playerName = "terminator";
    }

    String fileName = playerName + "-" + mode + "-" + difficulty + ".json";
    return saveFolder.resolve(fileName);
  }

  private GameSession createGameSession(GameSaveData gameData) throws GameSaveException {
    if (gameData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    Difficulty difficulty = parseDifficulty(gameData.difficulty());
    GameMode mode = parseMode(gameData.mode());

    Player player = createPlayer(gameData.player());
    List<Stock> stocks = createStocks(gameData.stocks());
    Exchange exchange = createExchange(gameData.exchange(), stocks, difficulty);
    restoreShares(gameData.shares(), player, exchange);
    restoreTransactions(gameData.transactions(), player, exchange);
    List<BigDecimal> netWorthHistory = createNetWorthHistory(gameData.netWorthHistory());

    return new GameSession(player, exchange, difficulty, mode, netWorthHistory);
  }

  private Difficulty parseDifficulty(String name) throws GameSaveException {
    if (name == null || name.isBlank()) {
      return Difficulty.NORMAL;
    }
    try {
      return Difficulty.valueOf(name);
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Unknown difficulty in save file: " + name, e);
    }
  }

  private GameMode parseMode(String name) throws GameSaveException {
    if (name == null || name.isBlank()) {
      return GameMode.SANDBOX;
    }
    try {
      return GameMode.valueOf(name);
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Unknown game mode in save file: " + name, e);
    }
  }

  private Player createPlayer(PlayerSaveData playerData) throws GameSaveException {
    if (playerData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    try {
      return new Player(
          playerData.name(),
          new BigDecimal(playerData.startingMoney()),
          new BigDecimal(playerData.money()));
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore player data", e);
    }
  }

  private List<Stock> createStocks(List<StockSaveData> stockData) throws GameSaveException {
    if (stockData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    List<Stock> stocks = new ArrayList<>();

    for (StockSaveData data : stockData) {
      stocks.add(createStock(data));
    }
    return stocks;
  }

  private Exchange createExchange(
      ExchangeSaveData exchangeData, List<Stock> stocks, Difficulty difficulty)
      throws GameSaveException {
    if (exchangeData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    try {
      return new Exchange(
          exchangeData.name(), stocks, exchangeData.week(), difficulty.getVolatility());
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore exchange data", e);
    }
  }

  private Stock createStock(StockSaveData stockData) throws GameSaveException {
    if (stockData == null || stockData.prices() == null || stockData.prices().isEmpty()) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    try {
      Stock stock =
          new Stock(
              stockData.symbol(),
              stockData.company(),
              new BigDecimal(stockData.prices().getFirst()));

      for (int i = 1; i < stockData.prices().size(); i++) {
        stock.addNewSalesPrice(new BigDecimal(stockData.prices().get(i)));
      }

      return stock;
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Incomplete stock save data", e);
    }
  }

  private void restoreShares(List<ShareSaveData> shareData, Player player, Exchange exchange)
      throws GameSaveException {
    if (shareData == null) {
      return;
    }

    try {
      for (ShareSaveData data : shareData) {
        Share share =
            new Share(
                exchange.getStock(data.stockSymbol()),
                new BigDecimal(data.quantity()),
                new BigDecimal(data.purchasePrice()));
        player.getPortfolio().addShare(share);
      }
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore shares", e);
    }
  }

  private void restoreTransactions(
      List<TransactionSaveData> transactionData, Player player, Exchange exchange)
      throws GameSaveException {
    if (transactionData == null) {
      return;
    }

    try {
      for (TransactionSaveData data : transactionData) {
        Transaction transaction = createTransaction(data, exchange);
        if (data.committed()) {
          player.getTransactionArchive().addRestored(transaction);
        } else {
          player.getTransactionArchive().add(transaction);
        }
      }
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore transaction history.", e);
    }
  }

  private Transaction createTransaction(TransactionSaveData data, Exchange exchange) {
    if (data == null) {
      throw new IllegalArgumentException("data cannot be null");
    }

    Stock stock = exchange.getStock(data.stockSymbol());
    Share share =
        new Share(stock, new BigDecimal(data.quantity()), new BigDecimal(data.purchasePrice()));

    if ("Purchase".equals(data.type())) {
      return TransactionFactory.createPurchase(share, data.week());
    }

    if ("Sale".equals(data.type())) {
      return TransactionFactory.createSale(share, data.week());
    }

    throw new IllegalArgumentException("Unknown transaction type: " + data.type());
  }

  private List<BigDecimal> createNetWorthHistory(List<String> netWorthHistoryData)
      throws GameSaveException {
    if (netWorthHistoryData == null) {
      return List.of();
    }

    try {
      List<BigDecimal> netWorthHistory = new ArrayList<>();
      for (String value : netWorthHistoryData) {
        netWorthHistory.add(new BigDecimal(value));
      }
      return netWorthHistory;
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore net worth history.", e);
    }
  }
}
