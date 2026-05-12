package edu.ntnu.idi.idatt.millions.file.save.dto;

import java.util.List;

/**
 * Data transfer object representing a saved stock and its price history.
 */
public class StockSaveData {
  private String symbol;
  private String company;
  private List<String> prices;

  public StockSaveData() {
  }

  public StockSaveData(String symbol, String company, List<String> prices) {
    this.symbol = symbol;
    this.company = company;
    this.prices = prices;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public List<String> getPrices() {
    return prices;
  }

  public void setPrices(List<String> prices) {
    this.prices = prices;
  }
}
