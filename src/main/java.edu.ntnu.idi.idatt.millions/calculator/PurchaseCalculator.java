package calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PurchaseCalculator implements TransactionCalculator{

  private final BigDecimal purchasePrice;
  private final BigDecimal quantity;
  private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.005");

  public PurchaseCalculator(Share share) {
    this.purchasePrice = share.getPurchasePrice();
    this.quantity = share.getQuantity();
  }

  @Override
  public BigDecimal calculateGross() {
    return purchasePrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal calculateCommission() {
    return calculateGross().multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal calculateTax() {
    return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal calculateTotal() {
    return calculateGross()
            .add(calculateCommission())
            .setScale(2, RoundingMode.HALF_UP);
  }
}
