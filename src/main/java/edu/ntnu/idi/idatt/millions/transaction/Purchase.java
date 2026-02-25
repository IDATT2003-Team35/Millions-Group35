package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.PurchaseCalculator;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;

import java.math.BigDecimal;

/**
 * Represents a purchase transaction of a share in the game.
 * Handles the logic for completing the purchase, including checking if the player
 * has sufficient funds, deducting the cost, and adding the share to the portfolio.
 */
public class Purchase extends Transaction {

  /**
   * Creates a new purchase transaction.
   *
   * @param share the share to be purchased
   * @param week the week the transaction takes place
   */
  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  /**
   * Executes the purchase logic for a given player.
   * Calculates the total cost, deducts the amount from the player's balance,
   * and adds the share to the player's portfolio.
   *
   * @param player the player executing the purchase
   * @throws IllegalStateException if the player does not have enough money to complete the purchase
   */
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