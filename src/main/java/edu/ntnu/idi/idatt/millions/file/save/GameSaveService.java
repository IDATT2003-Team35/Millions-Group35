package edu.ntnu.idi.idatt.millions.file.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ntnu.idi.idatt.millions.file.save.dto.*;
import edu.ntnu.idi.idatt.millions.model.*;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
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
    throw new GameSaveException("not implemented");
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
}
