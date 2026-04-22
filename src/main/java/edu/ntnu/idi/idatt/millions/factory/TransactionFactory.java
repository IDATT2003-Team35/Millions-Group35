package edu.ntnu.idi.idatt.millions.factory;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;

public class TransactionFactory {
  private TransactionFactory() {
  }

  public static Purchase createPurchase(Share share, int week) {
    validateInput(share, week);
    return new Purchase(share, week);
  }

  public static Sale createSale(Share share, int week) {
    validateInput(share, week);
    return new Sale(share, week);
  }
  private static void validateInput(Share share, int week) {
    if (share == null) {
      throw new IllegalArgumentException("Share cannot be null");
    }
    if (week <= 0) {
      throw new IllegalArgumentException("Week must be positive");
    }
  }
}
