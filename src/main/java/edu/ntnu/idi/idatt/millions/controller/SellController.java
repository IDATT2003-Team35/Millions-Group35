package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.PortfolioHolding;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.model.calculator.SaleCalculator;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.view.SellView;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
import java.util.List;

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
  private final PortfolioHolding holding;

  /**
   * Creates a controller for a sell order popup.
   *
   * @param view view used by the popup
   * @param dialogStage stage containing the popup
   * @param session active game session used to perform the sale
   * @param share share being sold
   * @throws IllegalArgumentException if any argument is {@code null}
   */
  public SellController(SellView view, Stage dialogStage, GameSession session, PortfolioHolding holding) {
    if (view == null) {
      throw new IllegalArgumentException("view cannot be null");
    }
    if (dialogStage == null) {
      throw new IllegalArgumentException("dialogStage cannot be null");
    }
    if (session == null) {
      throw new IllegalArgumentException("session cannot be null");
    }
    if (holding == null) {
      throw new IllegalArgumentException("share cannot be null");
    }

    this.view = view;
    this.dialogStage = dialogStage;
    this.session = session;
    this.holding = holding;

    populate();
    wireButtons();
  }

  private void populate() {
    Stock stock = holding.getStock();

    view.setStockSymbol(stock.getSymbol());
    view.setCompanyName(stock.getCompany());
    view.setQuantity(holding.getQuantity().toPlainString());
    view.setPurchasePrice(holding.getAveragePurchasePrice().toPlainString());
    view.setCurrentPrice(holding.getCurrentPrice().toPlainString());
    view.setGainLoss(holding.getTotalGainLoss().toPlainString());
    view.setEstimatedRevenue(holding.getCurrentValue().toPlainString());
  }

  private void wireButtons() {
    view.getCancelButton().setOnAction(e -> dialogStage.close());
    view.getConfirmButton().setOnAction(e -> handleSell());
  }

  private void handleSell() {
    view.clearErrorMessage();

    try {
      BigDecimal quantity = new BigDecimal(view.getQuantityToSell().trim());
      List<Transaction> transaction = session.sellStock(holding.getSymbol(), quantity);
      dialogStage.close();
      showReceipt(transaction);
    } catch (NumberFormatException e) {
      view.setErrorMessage("Quantity must be a valid number");
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.setErrorMessage(e.getMessage());
    }
  }

  private void showReceipt(List<Transaction> transactions) {
    TransactionReceiptView receiptView = new TransactionReceiptView();

    Stage receiptStage = new Stage();
    receiptStage.initModality(Modality.APPLICATION_MODAL);
    receiptStage.initOwner(dialogStage.getOwner());
    receiptStage.initStyle(StageStyle.UNDECORATED);
    receiptStage.setTitle("Transaction Receipt");
    receiptStage.setScene(new Scene(receiptView.getRoot()));
    new TransactionReceiptController(receiptView, receiptStage, transactions);
    receiptStage.showAndWait();
  }
}
