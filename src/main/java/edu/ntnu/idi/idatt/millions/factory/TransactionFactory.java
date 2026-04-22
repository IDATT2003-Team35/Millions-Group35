package edu.ntnu.idi.idatt.millions.factory;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;

/**
 * Factory class for creating transaction objects.
 * Provides centralized creation and validation of purchases and sales.
 */
public final class TransactionFactory {

  /**
   * Private constructor to prevent instantiation of this utility class.
   */
  private TransactionFactory() {
  }

  /**
   * Creates a new purchase transaction for the given share and week.
   *
   * @param share the share to be purchased
   * @param week the week in which the transaction is created
   * @return a new {@link Purchase} transaction
   * @throws IllegalArgumentException if share is {@code null} or week is not positive
   */
  public static Purchase createPurchase(Share share, int week) {
    validateInput(share, week);
    return new Purchase(share, week);
  }

  /**
   * Creates a new sale transaction for the given share and week.
   *
   * @param share the share to be sold
   * @param week the week in which the transaction is created
   * @return a new {@link Sale} transaction
   * @throws IllegalArgumentException if share is {@code null} or week is not positive
   */
  public static Sale createSale(Share share, int week) {
    validateInput(share, week);
    return new Sale(share, week);
  }

  /**
   * Validates common input used when creating transactions.
   *
   * @param share the share used in the transaction
   * @param week the transaction week
   * @throws IllegalArgumentException if share is {@code null} or week is not positive
   */
  private static void validateInput(Share share, int week) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }
  }
}
