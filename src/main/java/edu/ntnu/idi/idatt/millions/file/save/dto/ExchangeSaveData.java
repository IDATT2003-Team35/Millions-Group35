package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing saved exchange state.
 */
public record ExchangeSaveData(
        String name,
        int week
) {
}