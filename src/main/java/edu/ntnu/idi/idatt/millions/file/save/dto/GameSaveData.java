package edu.ntnu.idi.idatt.millions.file.save.dto;

import java.util.List;

/**
 * Data transfer object representing a complete saved game.
 *
 * @param player the saved player state
 * @param exchange the saved exchange state
 * @param stocks the saved stock data
 * @param shares the saved portfolio shares
 * @param transactions the saved transaction history
 * @param netWorthHistory the saved net worth history values
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
