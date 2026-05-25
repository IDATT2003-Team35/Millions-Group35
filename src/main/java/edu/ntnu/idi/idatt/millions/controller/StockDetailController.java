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
public class StockDetailController extends PageController {

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
    super(session, mainController);
    this.view = new StockDetailView(session);

    wireBack();
    wireBuy();
  }

  /**
   * Returns the stock detail view managed by this controller.
   *
   * @return the stock detail view
   */
  @Override
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
