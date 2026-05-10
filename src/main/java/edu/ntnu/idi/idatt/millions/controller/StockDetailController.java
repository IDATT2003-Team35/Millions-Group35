package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.StockDetailView;

/**
 * Controller for the stock detail view.
 */
public class StockDetailController {

  private final GameSession session;
  private final MainController mainController;
  private final StockDetailView view;

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
    view.displayStock(stock);
  }

  private void wireBack() {
    view.getBackButton().setOnAction(e -> mainController.showMarket());
  }
}
