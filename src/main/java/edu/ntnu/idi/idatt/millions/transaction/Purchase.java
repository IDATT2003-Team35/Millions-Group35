package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.PurchaseCalculator;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;

import java.math.BigDecimal;

public class Purchase extends Transaction{

  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  @Override
  protected void executeTransaction(Player player) {
    BigDecimal totalCost = getCalculator().calculateTotal();

    if (player.getMoney().compareTo(totalCost) < 0) {
      throw new IllegalStateException("Player does not have enough money for purchase");
    }

    player.withdrawMoney(totalCost);
    player.getPortfolio().addShare(getShare());
  }
}
