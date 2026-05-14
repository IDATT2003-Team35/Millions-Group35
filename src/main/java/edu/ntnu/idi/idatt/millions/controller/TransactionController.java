package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;

import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import edu.ntnu.idi.idatt.millions.view.TransactionView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

/**
 * Controller for the portfolio view.
 */
public class TransactionController {

  private final GameSession session;
  private final MainController mainController;
  private final TransactionView view;

  /**
   * Creates a new portfolio controller and the view it manages.
   *
   * @param session the active game session, must not be null
   * @param mainController the parent controller used for navigation must not be null
   * @throws IllegalArgumentException if session or mainController is null
   */
  public TransactionController(GameSession session, MainController mainController) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    if (mainController == null) {
      throw new IllegalArgumentException("MainController cannot be null");
    }
    this.session = session;
    this.mainController = mainController;
    this.view = new TransactionView(session);

    view.setOnUpdate(this::applyFilter);
    wireSearch();
    wireTransactionHandler();
    applyFilter();
  }

  public TransactionView getView() {
    return view;
  }

  private void wireTransactionHandler() {
    view.setViewTransactionHandler(this::showTransactionReceipt);
  }

  private void wireSearch() {
    view.getSearchField().textProperty()
        .addListener((obs, oldValue, newValue) -> applyFilter());
  }

  private void applyFilter() {
    String query = view.getSearchField().getText();
    List<Transaction> all = session.getPlayer().getTransactionArchive().getAll();
    List<Transaction> shown = (query == null || query.isBlank())
        ? all
        : filter(all, query);
    view.setTransactions(shown);
  }

  private static List<Transaction> filter(List<Transaction> all, String query) {
    String q = query.toLowerCase();
    return all.stream()
        .filter(t -> {
          String type = t instanceof Purchase ? "buy" : "sell";
          String symbol = t.getShare().getStock().getSymbol().toLowerCase();
          String week = String.valueOf(t.getWeek());
          return type.contains(q) || symbol.contains(q) || week.contains(q);
        })
        .toList();
  }

  private void showTransactionReceipt(Transaction transaction) {
    TransactionReceiptView receiptView = new TransactionReceiptView();

    Stage receiptStage = new Stage();
    receiptStage.initModality(Modality.APPLICATION_MODAL);
    receiptStage.initOwner(view.getScene().getWindow());
    receiptStage.initStyle(StageStyle.UNDECORATED);
    receiptStage.setTitle("Transaction Receipt");
    receiptStage.setScene(new Scene(receiptView.getRoot()));
    new TransactionReceiptController(receiptView, receiptStage, transaction);
    receiptStage.showAndWait();
  }
}


