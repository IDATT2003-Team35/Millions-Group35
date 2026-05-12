package edu.ntnu.idi.idatt.millions.file.save.dto;

/**
 * Data transfer object representing saved exchange state.
 */
public class ExchangeSaveData {
  private String name;
  private int week;

  public ExchangeSaveData() {
  }

  public ExchangeSaveData(String name, int week) {
    this.name = name;
    this.week = week;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getWeek() {
    return week;
  }

  public void setWeek(int week) {
    this.week = week;
  }
}
