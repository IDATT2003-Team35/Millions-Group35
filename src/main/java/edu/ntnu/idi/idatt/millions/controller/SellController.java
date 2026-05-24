package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Styles;
import edu.ntnu.idi.idatt.millions.view.SellView;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;

/**
 * Controller for the sell order popup.
 *
 * <p>The controller fills the view with share and price data, performs the
 * sale through the game session, and opens a receipt when the transaction
 * succeeds.</p>
 */
public class SellController {
  private final SellView view;
  private final Stage dialogStage;
  private final GameSession session;
  private final Share share;

  /**
   * Creates a controller for a sell order popup.
   *
   * @param view view used by the popup
   * @param dialogStage stage containing the popup
   * @param session active game session used to perform the sale
   * @param share share being sold
   * @throws IllegalArgumentException if any argument is {@code null}
   */
  public SellController(SellView view, Stage dialogStage, GameSession session, Share share) {
    if (view == null) {
      throw new IllegalArgumentException("view cannot be null");
    }
    if (dialogStage == null) {
      throw new IllegalArgumentException("dialogStage cannot be null");
    }
    if (session == null) {
      throw new IllegalArgumentException("session cannot be null");
    }
    if (share == null) {
      throw new IllegalArgumentException("share cannot be null");
    }

    this.view = view;
    this.dialogStage = dialogStage;
    this.session = session;
    this.share = share;

    Styles.applyTo(dialogStage.getScene());
    populate();
    wireButtons();
  }

  private void populate() {
    Stock stock = share.getStock();
    SaleCalculator calculator = new SaleCalculator(share);

    BigDecimal gainLoss = share.getNetGainLoss();

    view.setStockSymbol(stock.getSymbol());
    view.setCompanyName(stock.getCompany());
    view.setQuantity(share.getQuantity().toPlainString());
    view.setPurchasePrice(share.getPurchasePrice().toPlainString());
    view.setCurrentPrice(stock.getSalesPrice().toPlainString());
    view.setGross(calculator.calculateGross().toPlainString());
    view.setCommission(calculator.calculateCommission().toPlainString());
    view.setTax(calculator.calculateTax().toPlainString());
    view.setCashReceived(calculator.calculateTotal().toPlainString());
    view.setGainLoss(Money.formatWithSign(gainLoss), gainLoss.signum());
  }

  private void wireButtons() {
    view.getCancelButton().setOnAction(e -> dialogStage.close());
    view.getConfirmButton().setOnAction(e -> handleSell());
  }

  private void handleSell() {
    view.clearErrorMessage();

    try {
      Transaction transaction = session.sellShare(share);
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
