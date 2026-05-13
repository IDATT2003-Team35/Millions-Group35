package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing saved player state.
 */
public record PlayerSaveData(
        String name,
        String startingMoney,
        String money
) {
}
