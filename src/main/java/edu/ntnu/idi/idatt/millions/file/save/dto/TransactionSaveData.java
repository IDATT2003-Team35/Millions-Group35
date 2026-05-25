package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing a saved transaction.
 *
 * @param type the transaction type
 * @param stockSymbol the symbol of the stock involved in the transaction
 * @param quantity the transaction quantity
 * @param purchasePrice the price per share used by the transaction
 * @param week the week when the transaction happened
 * @param committed whether the transaction was committed
 */
public record TransactionSaveData(
    String type,
    String stockSymbol,
    String quantity,
    String purchasePrice,
    int week,
    boolean committed) {}
