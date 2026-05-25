package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing saved exchange state.
 *
 * @param name the exchange name
 * @param week the current trading week
 */
public record ExchangeSaveData(String name, int week) {}
