package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.view.BuyView;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;

public class BuyController {
  private final BuyView view;
  private final Stage dialogStage;
  private final GameSession session;
  private final Stock stock;

  public BuyController(BuyView view, Stage dialogStage, GameSession session, Stock stock) {
    if (view == null) {
      throw new IllegalArgumentException("view cannot be null");
    }
    if (dialogStage == null) {
      throw new IllegalArgumentException("dialogStage cannot be null");
    }
    if (session == null) {
      throw new IllegalArgumentException("session cannot be null");
    }
    if (stock == null) {
      throw new IllegalArgumentException("stock cannot be null");
    }

    this.view = view;
    this.dialogStage = dialogStage;
    this.session = session;
    this.stock = stock;

    populate();
    wireButtons();
    wireQuantityListener();
  }

  private void populate() {
    view.setStockSymbol(stock.getSymbol());
    view.setStockPrice(stock.getSalesPrice().toPlainString());
    view.setTotalCost("0.00");
    view.setAvailableCash(session.getPlayer().getMoney().toPlainString());
  }

  private void wireButtons() {
    view.getCancelButton().setOnAction(e -> dialogStage.close());
    view.getConfirmButton().setOnAction(e -> handleBuy());
  }

  private void wireQuantityListener() {
    view.getQuantityField().textProperty().addListener(
            (obs, oldTotCost, newTotCost) -> updateTotalCost()
    );
  }

  private void updateTotalCost() {
    String quantityText = view.getQuantityField().getText().trim();

    if (quantityText.isEmpty()) {
      view.setTotalCost("0.00");
      return;
    }

    try {
      BigDecimal quantity = new BigDecimal(quantityText);
      if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
        view.setTotalCost("0.00");
        return;
      }

      BigDecimal totalCost = stock.getSalesPrice().multiply(quantity);
      view.setTotalCost(totalCost.toPlainString());
    } catch (NumberFormatException e) {
      view.setTotalCost("0.00");
    }
  }

  private void handleBuy() {
    view.clearErrorMessage();

    String quantityText = view.getQuantityField().getText().trim();
    BigDecimal quantity;
    try {
      quantity = new BigDecimal(quantityText);
    } catch (NumberFormatException e) {
      view.setErrorMessage("Please enter valid quantity");
      return;
    }

    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
      view.setErrorMessage("Quantity must be greater than 0");
      return;
    }

    try {
      Transaction transaction = session.buyStock(stock.getSymbol(), quantity);
      dialogStage.close();
      showReceipt(transaction);
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.setErrorMessage(e.getMessage());
    }
  }

  private void showReceipt(Transaction transaction) {
    TransactionReceiptView receiptView = new TransactionReceiptView();

    Stage receiptStage = new Stage();
    receiptStage.initModality(Modality.APPLICATION_MODAL);
    receiptStage.initOwner(dialogStage.getOwner());
    receiptStage.initStyle(StageStyle.UNDECORATED);
    receiptStage.setTitle("Transaction Receipt");
    receiptStage.setScene(new Scene(receiptView.getRoot()));
    new TransactionReceiptController(receiptView, receiptStage, transaction);
    receiptStage.showAndWait();
  }
}
