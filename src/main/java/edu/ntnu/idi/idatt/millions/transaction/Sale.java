package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;

import java.math.BigDecimal;

public class Sale extends Transaction{

  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  @Override
  protected void executeTransaction(Player player) {
    BigDecimal totalValue = getCalculator().calculateTotal();

    if (!player.getPortfolio().contains(getShare())) {
      throw new IllegalArgumentException("Player does not have this share");
    }

    player.addMoney(totalValue);
    player.getPortfolio().removeShare(getShare());
  }
}

