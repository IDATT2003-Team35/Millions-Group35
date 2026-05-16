package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;

import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.view.TransactionReceiptView;
import edu.ntnu.idi.idatt.millions.view.TransactionView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.math.BigDecimal;
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

    BigDecimal totalBought = BigDecimal.ZERO;
    BigDecimal totalSold = BigDecimal.ZERO;
    int buyCount = 0;
    int sellCount = 0;
    for (Transaction t : all) {
      BigDecimal gross = t.getCalculator().calculateGross();
      if (t instanceof Purchase) {
        totalBought = totalBought.add(gross);
        buyCount++;
      } else {
        totalSold = totalSold.add(gross);
        sellCount++;
      }
    }
    view.setTotalBought(Money.format(totalBought), buyCount);
    view.setTotalSold(Money.format(totalSold), sellCount);
    view.setNetActivity(Money.format(totalBought.subtract(totalSold)));
    view.setRecordsCount(all.size());
    view.setFilterCounts(all.size(), buyCount, sellCount);

    List<Transaction> searched = (query == null || query.isBlank())
        ? all
        : filter(all, query);

    String tab = view.getSelectedFilter();
    List<Transaction> shown = switch (tab) {
      case "BUYS" -> searched.stream().filter(t -> t instanceof Purchase).toList();
      case "SELLS" -> searched.stream().filter(t -> !(t instanceof Purchase)).toList();
      default -> searched;
    };
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


