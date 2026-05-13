package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing a saved transaction.
 */
public record TransactionSaveData(
        String type,
        String stockSymbol,
        String quantity,
        String purchasePrice,
        int week,
        boolean committed
) {
}
