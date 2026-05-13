package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing a saved portfolio share.
 */
public record ShareSaveData(
        String stockSymbol,
        String quantity,
        String purchasePrice
) {
}
