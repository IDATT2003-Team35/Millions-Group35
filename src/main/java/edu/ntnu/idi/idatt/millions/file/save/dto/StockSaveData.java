package edu.ntnu.idi.idatt.millions.file.save.dto;

import java.util.List;

/**
 * Data transfer object representing a saved stock and its price history.
 *
 * @param symbol the stock symbol
 * @param company the company name
 * @param prices the saved historical stock prices
 */
public record StockSaveData(String symbol, String company, List<String> prices) {}
