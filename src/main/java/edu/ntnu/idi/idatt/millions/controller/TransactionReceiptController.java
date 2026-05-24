package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.calculator.TransactionCalculator;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import edu.ntnu.idi.idatt.millions.util.Styles;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import javafx.stage.Stage;

import java.math.BigDecimal;


/**
 * Controller for the transaction receipt popup.
 *
 * <p>The controller reads a completed transaction, calculates the displayed
 * receipt values, and wires the close button.</p>
 */
public class TransactionReceiptController {
  private final TransactionReceiptView view;
  private final Stage dialogStage;
  private final Transaction transaction;

  /**
   * Creates a controller for a transaction receipt popup.
   *
   * @param view view used by the receipt popup
   * @param dialogStage stage containing the popup
   * @param transaction completed transaction to display
   * @throws IllegalArgumentException if any argument is {@code null}
   */
  public TransactionReceiptController(
      TransactionReceiptView view,
      Stage dialogStage,
      Transaction transaction
  ) {
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    if (dialogStage == null) {
      throw new IllegalArgumentException("Dialog stage cannot be null");
    }
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null");
    }

    this.view = view;
    this.dialogStage = dialogStage;
    this.transaction = transaction;

    Styles.applyTo(dialogStage.getScene());
    populate();
    wireButtons();
  }

  private void populate() {
    Share share = transaction.getShare();
    Stock stock = share.getStock();
    TransactionCalculator calculator = transaction.getCalculator();

    view.setTitle(getReceiptTitle());
    view.setStockSymbol(stock.getSymbol());
    view.setCompanyName(stock.getCompany());
    view.setQuantity(share.getQuantity().toPlainString());
    view.setPrice(getTransactionPricePerShare(share).toPlainString());
    view.setGross(calculator.calculateGross().toPlainString());
    view.setCommission(calculator.calculateCommission().toPlainString());
    view.setTax(calculator.calculateTax().toPlainString());
    view.setTaxVisible(transaction instanceof Sale);
    view.setTotalLabel(getTotalLabel());
    view.setTotal(calculator.calculateTotal().toPlainString());
    view.setWeek(String.valueOf(transaction.getWeek()));

    populateSaleSummary(share, calculator);
  }

  /**
   * Adds the cost basis row and the gain/loss header for Sale transactions.
   * For Purchase transactions both are hidden, since no profit is realized yet.
   */
  private void populateSaleSummary(Share share, TransactionCalculator calculator) {
    boolean isSale = transaction instanceof Sale;
    view.setCostBasisVisible(isSale);
    view.setGainHeaderVisible(isSale);
    if (!isSale) {
      return;
    }

    BigDecimal costBasis = share.getPurchasePrice().multiply(share.getQuantity());
    BigDecimal cashReceived = calculator.calculateTotal();
    BigDecimal gain = cashReceived.subtract(costBasis);
    BigDecimal returnPercent = Percentages.change(costBasis, cashReceived);

    view.setCostBasis(costBasis.toPlainString());
    view.setGainHeader(
        Money.formatWithSign(gain),
        Percentages.format(returnPercent),
        gain.signum()
    );
  }

  private BigDecimal getTransactionPricePerShare(Share share) {
    if (transaction instanceof Sale) {
      return share.getStock().getSalesPrice();
    }
    return share.getPurchasePrice();
  }

  private void wireButtons() {
    view.getCloseButton().setOnAction(e -> dialogStage.close());
  }

  private String getReceiptTitle() {
    if (transaction instanceof Purchase) {
      return "BUY RECEIPT";
    }
    if (transaction instanceof Sale) {
      return "SELL RECEIPT";
    }
    return "TRANSACTION RECEIPT";
  }

  private String getTotalLabel() {
    if (transaction instanceof Purchase) {
      return "Total Cost ($):";
    }
    if (transaction instanceof Sale) {
      return "Cash Received ($):";
    }
    return "Total ($):";
  }
}
