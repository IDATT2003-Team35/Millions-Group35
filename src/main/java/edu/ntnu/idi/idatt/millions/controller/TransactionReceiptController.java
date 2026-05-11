package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.calculator.TransactionCalculator;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Sale;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import javafx.stage.Stage;


public class TransactionReceiptController {
  private final TransactionReceiptView view;
  private final Stage dialogStage;
  private final Transaction transaction;

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
    view.setPrice(share.getPurchasePrice().toPlainString());
    view.setGross(calculator.calculateGross().toPlainString());
    view.setCommission(calculator.calculateCommission().toPlainString());
    view.setTax(calculator.calculateTax().toPlainString());
    view.setTaxVisible(transaction instanceof Sale);
    view.setTotalLabel(getTotalLabel());
    view.setTotal(calculator.calculateTotal().toPlainString());
    view.setWeek(String.valueOf(transaction.getWeek()));
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
      return "Total Revenue ($):";
    }
    return "Total ($):";
  }
}
