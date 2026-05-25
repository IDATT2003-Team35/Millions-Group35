package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.BuyView;
import edu.ntnu.idi.idatt.millions.view.StockDetailView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** Controller for the stock detail view. */
public class StockDetailController {

  private final GameSession session;
  private final MainController mainController;
  private final StockDetailView view;
  private Stock currentStock;

  /**
   * Creates a new stock detail controller and the view it manages.
   *
   * @param session the active game session; must not be null
   * @param mainController the parent controller used for navigation; must not be null
   * @throws IllegalArgumentException if session or mainController is null
   */
  public StockDetailController(GameSession session, MainController mainController) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    if (mainController == null) {
      throw new IllegalArgumentException("MainController cannot be null");
    }
    this.session = session;
    this.mainController = mainController;
    this.view = new StockDetailView(session);

    wireBack();
    wireBuy();
  }

  public StockDetailView getView() {
    return view;
  }

  /**
   * Updates the view to display the given stock.
   *
   * @param stock the stock to display
   */
  public void display(Stock stock) {
    currentStock = stock;
    view.displayStock(stock);
  }

  private void wireBuy() {
    view.getBuyButton()
        .setOnAction(
            e -> {
              if (currentStock == null) {
                return;
              }

              BuyView buyView = new BuyView();

              Stage dialogStage = new Stage();
              dialogStage.initModality(Modality.APPLICATION_MODAL);
              dialogStage.initOwner(view.getScene().getWindow());
              dialogStage.initStyle(StageStyle.UNDECORATED);
              dialogStage.setTitle("Buy Order");
              dialogStage.setScene(new Scene(buyView.getRoot()));
              new BuyController(buyView, dialogStage, session, currentStock);
              dialogStage.showAndWait();
            });
  }

  private void wireBack() {
    view.getBackButton().setOnAction(e -> mainController.showMarket());
  }
}
