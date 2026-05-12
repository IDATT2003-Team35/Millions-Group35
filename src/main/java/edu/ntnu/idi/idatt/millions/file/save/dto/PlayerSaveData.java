package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing saved player state.
 */
public class PlayerSaveData {
  private String name;
  private String startingMoney;
  private String money;

  public PlayerSaveData() {
  }

  public PlayerSaveData(String name, String startingMoney, String money) {
    this.name = name;
    this.startingMoney = startingMoney;
    this.money = money;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStartingMoney() {
    return startingMoney;
  }

  public void setStartingMoney(String startingMoney) {
    this.startingMoney = startingMoney;
  }

  public String getMoney() {
    return money;
  }

  public void setMoney(String money) {
    this.money = money;
  }
}
