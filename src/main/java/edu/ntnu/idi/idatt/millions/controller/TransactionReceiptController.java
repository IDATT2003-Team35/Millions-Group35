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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import javafx.stage.Stage;

/**
 * Controller for the transaction receipt popup.
 *
 * <p>The controller reads one completed transaction, or a group of transactions from one
 * user-facing sale, calculates the displayed receipt values, and wires the close button.
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
      TransactionReceiptView view, Stage dialogStage, Transaction transaction) {
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

  /**
   * Creates a controller for a receipt that summarizes several completed transactions.
   *
   * <p>This is used when one user-facing sale spans multiple purchase lots and therefore produces
   * more than one sale transaction.
   *
   * @param view view used by the receipt popup
   * @param dialogStage stage containing the popup
   * @param transactions completed transactions to summarize
   * @throws IllegalArgumentException if any argument is invalid
   */
  public TransactionReceiptController(
      TransactionReceiptView view, Stage dialogStage, List<Transaction> transactions) {
    if (view == null) {
      throw new IllegalArgumentException("View cannot be null");
    }
    if (dialogStage == null) {
      throw new IllegalArgumentException("Dialog stage cannot be null");
    }
    if (transactions == null || transactions.isEmpty()) {
      throw new IllegalArgumentException("Transactions cannot be null or empty");
    }
    if (transactions.stream().anyMatch(transaction -> transaction == null)) {
      throw new IllegalArgumentException("Transactions cannot contain null values");
    }

    this.view = view;
    this.dialogStage = dialogStage;
    this.transaction = transactions.getFirst();

    populate(transactions);
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

    populateSaleSummary();
  }

  /**
   * Adds the cost basis row and the gain/loss header for Sale transactions.
   * For Purchase transactions both are hidden, since no profit is realized yet.
   */
  private void populateSaleSummary() {
    if (!(transaction instanceof Sale sale)) {
      view.setCostBasisVisible(false);
      view.setGainHeaderVisible(false);
      return;
    }
    view.setCostBasisVisible(true);
    view.setGainHeaderVisible(true);

    BigDecimal costBasis = sale.getCostBasis();
    BigDecimal gain = sale.getRealizedGain();
    BigDecimal returnPercent = Percentages.change(costBasis, sale.getCalculator().calculateTotal());

    view.setCostBasis(costBasis.toPlainString());
    view.setGainHeader(
        Money.formatWithSign(gain),
        Percentages.format(returnPercent),
        gain.signum()
    );
  }

  private void populate(List<Transaction> transactions) {
    Transaction firstTransaction = transactions.getFirst();
    Share firstShare = firstTransaction.getShare();
    Stock stock = firstShare.getStock();

    BigDecimal quantity = sum(transactions, transaction -> transaction.getShare().getQuantity());
    BigDecimal gross =
        sum(transactions, transaction -> transaction.getCalculator().calculateGross());
    BigDecimal commission =
        sum(transactions, transaction -> transaction.getCalculator().calculateCommission());
    BigDecimal tax = sum(transactions, transaction -> transaction.getCalculator().calculateTax());
    BigDecimal total =
        sum(transactions, transaction -> transaction.getCalculator().calculateTotal());

    BigDecimal price = gross.divide(quantity, 2, RoundingMode.HALF_UP);

    view.setTitle(getReceiptTitle());
    view.setStockSymbol(stock.getSymbol());
    view.setCompanyName(stock.getCompany());
    view.setQuantity(quantity.toPlainString());
    view.setPrice(price.toPlainString());
    view.setGross(gross.toPlainString());
    view.setCommission(commission.toPlainString());
    view.setTax(tax.toPlainString());
    view.setTaxVisible(firstTransaction instanceof Sale);
    view.setTotalLabel(getTotalLabel());
    view.setTotal(total.toPlainString());
    view.setWeek(String.valueOf(firstTransaction.getWeek()));
  }

  private BigDecimal sum(List<Transaction> transactions, Function<Transaction, BigDecimal> mapper) {
    return transactions.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
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
