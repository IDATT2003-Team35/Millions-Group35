package edu.ntnu.idi.idatt.millions.transaction;

import edu.ntnu.idi.idatt.millions.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;

import java.math.BigDecimal;

/**
 * Represents a sale transaction of a share in the game.
 * Handles the logic for completing the sale, including verifying that the player
 * owns the share, adding the total value to the player's balance, and removing
 * [cite_start]the share from the portfolio [cite: 232-235].
 */
public class Sale extends Transaction {

  /**
   * Creates a new sale transaction.
   *
   * @param share the share to be sold
   * @param week the week the transaction takes place
   */
  public Sale(Share share, int week) {
    super(share, week, new SaleCalculator(share));
  }

  /**
   * Executes the sale logic for a given player.
   * Calculates the total value, adds the amount to the player's balance,
   * [cite_start]and removes the share from the player's portfolio [cite: 232-235].
   *
   * @param player the player executing the sale
   * @throws IllegalStateException if the player does not own the share being sold
   */
  @Override
  protected void executeTransaction(Player player) {
    BigDecimal totalValue = getCalculator().calculateTotal();

    if (!player.getPortfolio().contains(getShare())) {
      throw new IllegalStateException("Player does not have this share");
    }

    player.addMoney(totalValue);
    player.getPortfolio().removeShare(getShare());
  }
}