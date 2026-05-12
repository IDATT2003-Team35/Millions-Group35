package edu.ntnu.idi.idatt.millions.file.save.dto;

import java.util.List;

/**
 * Data transfer object representing a complete saved game.
 */
public class GameSaveData {
  private PlayerSaveData player;
  private ExchangeSaveData exchange;
  private List<StockSaveData> stocks;
  private List<ShareSaveData> shares;
  private List<TransactionSaveData> transactions;
  private List<String> netWorthHistory;

  public GameSaveData() {
  }

  public GameSaveData(
          PlayerSaveData player,
          ExchangeSaveData exchange,
          List<StockSaveData> stocks,
          List<ShareSaveData> shares,
          List<TransactionSaveData> transactions,
          List<String> netWorthHistory
  ) {
    this.player = player;
    this.exchange = exchange;
    this.stocks = stocks;
    this.shares = shares;
    this.transactions = transactions;
    this.netWorthHistory = netWorthHistory;
  }

  public PlayerSaveData getPlayer() {
    return player;
  }

  public void setPlayer(PlayerSaveData player) {
    this.player = player;
  }

  public ExchangeSaveData getExchange() {
    return exchange;
  }

  public void setExchange(ExchangeSaveData exchange) {
    this.exchange = exchange;
  }

  public List<StockSaveData> getStocks() {
    return stocks;
  }

  public void setStocks(List<StockSaveData> stocks) {
    this.stocks = stocks;
  }

  public List<ShareSaveData> getShares() {
    return shares;
  }

  public void setShares(List<ShareSaveData> shares) {
    this.shares = shares;
  }

  public List<TransactionSaveData> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionSaveData> transactions) {
    this.transactions = transactions;
  }

  public List<String> getNetWorthHistory() {
    return netWorthHistory;
  }

  public void setNetWorthHistory(List<String> netWorthHistory) {
    this.netWorthHistory = netWorthHistory;
  }
}
