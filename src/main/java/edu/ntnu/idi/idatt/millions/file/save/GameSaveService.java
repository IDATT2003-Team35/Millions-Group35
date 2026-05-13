package edu.ntnu.idi.idatt.millions.file.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idi.idatt.millions.file.save.dto.*;
import edu.ntnu.idi.idatt.millions.model.*;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GameSaveService {
  private static final Path DEFAULT_SAVE_FOLDER = Path.of("saves");
  private final ObjectMapper objectMapper;
  private final Path saveFolder;

  public GameSaveService() throws GameSaveException {
    this(DEFAULT_SAVE_FOLDER);
  }

  public GameSaveService(Path saveFolder) throws GameSaveException {
    if (saveFolder == null) {
      throw new GameSaveException("Save folder cannot be null");
    }
    this.objectMapper = new ObjectMapper();
    this.saveFolder = saveFolder;
  }

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

    PlayerSaveData playerData = new PlayerSaveData(
            player.getName(),
            player.getStartingMoney().toPlainString(),
            player.getMoney().toPlainString()
    );

    ExchangeSaveData exchangeData = new ExchangeSaveData(
            exchange.getName(),
            exchange.getWeek()
    );

    List<StockSaveData> stockData = session.getExchange().getStocks().stream()
            .map(this::createStockSaveData)
            .toList();

    List<ShareSaveData> shareData = session.getPlayer().getPortfolio().getShares().stream()
            .map(this::createShareSaveData)
            .toList();

    List<TransactionSaveData> transactionData = session.getPlayer().getTransactionArchive().getAll().stream()
            .map(this::createTransactionSaveData)
            .toList();

    List<String> netWorthHistoryData = session.getNetWorthHistory().stream()
            .map(BigDecimal::toPlainString)
            .toList();

    return new GameSaveData(
            playerData,
            exchangeData,
            stockData,
            shareData,
            transactionData,
            netWorthHistoryData
    );
  }

  private StockSaveData createStockSaveData(Stock stock) {
    return new StockSaveData(
            stock.getSymbol(),
            stock.getCompany(),
            stock.getHistoricalPrices().stream()
                    .map(BigDecimal::toPlainString)
                    .toList()
    );
  }

  private ShareSaveData createShareSaveData(Share share) {
    return new ShareSaveData(
            share.getStock().getSymbol(),
            share.getQuantity().toPlainString(),
            share.getPurchasePrice().toPlainString()
    );
  }

  private TransactionSaveData createTransactionSaveData(Transaction transaction) {
    return new TransactionSaveData(
            transaction.getClass().getSimpleName(),
            transaction.getShare().getStock().getSymbol(),
            transaction.getShare().getQuantity().toPlainString(),
            transaction.getShare().getPurchasePrice().toPlainString(),
            transaction.getWeek(),
            transaction.isCommitted()
    );
  }

  private Path createSavePath(GameSession session) {
    int week = session.getExchange().getWeek();
    String playerName = session.getPlayer().getName()
            .trim()
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

    if (playerName.isBlank()) {
      playerName = "terminator";
    }

    String fileName = playerName + "-week-" + week + ".json";
    return saveFolder.resolve(fileName);
  }

  private GameSession createGameSession(GameSaveData gameData) throws GameSaveException {
    if (gameData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    Player player = createPlayer(gameData.player());
    List<Stock> stocks = createStocks(gameData.stocks());
    Exchange exchange = createExchange(gameData.exchange(), stocks);
    restoreShares(gameData.shares(), player, exchange);

    return new GameSession(player, exchange);
  }

  private Player createPlayer(PlayerSaveData playerData) throws GameSaveException {
    if (playerData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    try {
      return new Player(
              playerData.name(),
              new BigDecimal(playerData.startingMoney())
      );
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

  private Exchange createExchange(ExchangeSaveData exchangeData, List<Stock> stocks) throws GameSaveException {
    if (exchangeData == null) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    try {
      return new Exchange(exchangeData.name(), stocks);
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore exchange data", e);
    }
  }

  private Stock createStock(StockSaveData stockData) throws GameSaveException {
    if (stockData == null || stockData.prices() == null || stockData.prices().isEmpty()) {
      throw new GameSaveException("Save file does not contain game data.");
    }

    try {
      Stock stock = new Stock(
              stockData.symbol(),
              stockData.company(),
              new BigDecimal(stockData.prices().getFirst())
      );

      for (int i = 1; i < stockData.prices().size(); i++) {
        stock.addNewSalesPrice(new BigDecimal(stockData.prices().get(i)));
      }

      return stock;
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Incomplete stock save data", e);
    }
  }

  private void restoreShares(List<ShareSaveData> shareData, Player player, Exchange exchange) throws GameSaveException {
    if (shareData == null) {
      return;
    }

    try {
      for (ShareSaveData data : shareData) {
        Share share = new Share(
                exchange.getStock(data.stockSymbol()),
                new BigDecimal(data.quantity()),
                new BigDecimal(data.purchasePrice())
        );
        player.getPortfolio().addShare(share);
      }
    } catch (IllegalArgumentException e) {
      throw new GameSaveException("Could not restore shares", e);
    }
  }
}
