package edu.ntnu.idi.idatt.millions.file.save.dto;

import java.util.List;

/**
 * Data transfer object representing a complete saved game.
 */
public record GameSaveData(
        PlayerSaveData player,
        ExchangeSaveData exchange,
        List<StockSaveData> stocks,
        List<ShareSaveData> shares,
        List<TransactionSaveData> transactions,
        List<String> netWorthHistory
) {
}
