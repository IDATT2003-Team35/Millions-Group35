package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing a saved portfolio share.
 *
 * @param stockSymbol the symbol of the stock owned
 * @param quantity the owned quantity
 * @param purchasePrice the purchase price per share
 */
public record ShareSaveData(String stockSymbol, String quantity, String purchasePrice) {}
