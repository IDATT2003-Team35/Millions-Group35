package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.MarketView;

import java.util.List;

/**
 * Controller for the market view. Wires the search field to live filtering
 * and the table row click to opening a stock detail view.
 */
public class MarketController {

  private final GameSession session;
  private final MainController mainController;
  private final MarketView view;

  /**
   * Creates a new market controller and the market view it manages.
   *
   * @param session the active game session; must not be null
   * @param mainController the parent controller used to navigate to detail views; must not be null
   * @throws IllegalArgumentException if session or mainController is null
   */
  public MarketController(GameSession session, MainController mainController) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    if (mainController == null) {
      throw new IllegalArgumentException("MainController cannot be null");
    }
    this.session = session;
    this.mainController = mainController;
    this.view = new MarketView(session);

    wireSearch();
    wireRowClick();
  }

  public MarketView getView() {
    return view;
  }

  private void wireSearch() {
    view.getSearchField().textProperty().addListener((obs, oldValue, newValue) -> {
      List<Stock> all = session.getExchange().getStocks();
      List<Stock> shown = (newValue == null || newValue.isBlank())
          ? all
          : session.getExchange().findStocks(newValue);
      view.getStockTable().getItems().setAll(shown);
      view.getCountLabel().setText(
          "Showing " + shown.size() + " of " + all.size() + " stocks");
    });
  }

  private void wireRowClick() {
    view.getStockTable().setOnMouseClicked(e -> {
      Stock selected = view.getStockTable().getSelectionModel().getSelectedItem();
      if (selected == null) {
        return;
      }
    });
  }
}
