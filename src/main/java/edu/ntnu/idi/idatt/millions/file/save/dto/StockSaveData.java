package edu.ntnu.idi.idatt.millions.file.save.dto;

import java.util.List;

/**
 * Data transfer object representing a saved stock and its price history.
 */
public record StockSaveData(
        String symbol,
        String company,
        List<String> prices
) {
}
