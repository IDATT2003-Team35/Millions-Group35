package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.calculator.Share;

public class Sale extends Transaction{

  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  @Override
  protected void executeTransaction(Player player) {

  }
}

