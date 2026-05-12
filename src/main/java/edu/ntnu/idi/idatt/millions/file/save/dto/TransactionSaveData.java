package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing a saved transaction.
 */
public class TransactionSaveData {
  private String type;
  private String stockSymbol;
  private String quantity;
  private String purchasePrice;
  private int week;
  private boolean committed;

  public TransactionSaveData() {
  }

  public TransactionSaveData(
          String type,
          String stockSymbol,
          String quantity,
          String purchasePrice,
          int week,
          boolean committed
  ) {
    this.type = type;
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
    this.week = week;
    this.committed = committed;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStockSymbol() {
    return stockSymbol;
  }

  public void setStockSymbol(String stockSymbol) {
    this.stockSymbol = stockSymbol;
  }

  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public String getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(String purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public int getWeek() {
    return week;
  }

  public void setWeek(int week) {
    this.week = week;
  }

  public boolean isCommitted() {
    return committed;
  }

  public void setCommitted(boolean committed) {
    this.committed = committed;
  }
}
