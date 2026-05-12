package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing a saved portfolio share.
 */
public class ShareSaveData {
  private String stockSymbol;
  private String quantity;
  private String purchasePrice;

  public ShareSaveData() {
  }

  public ShareSaveData(String stockSymbol, String quantity, String purchasePrice) {
    this.stockSymbol = stockSymbol;
    this.quantity = quantity;
    this.purchasePrice = purchasePrice;
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
}
