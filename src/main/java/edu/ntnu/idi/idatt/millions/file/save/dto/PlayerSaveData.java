package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing saved player state.
 *
 * @param name the player's name
 * @param startingMoney the player's original starting balance
 * @param money the player's current cash balance
 */
public record PlayerSaveData(String name, String startingMoney, String money) {}
