package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.MarketView;
import javafx.scene.control.TableRow;

import java.util.List;

/**
 * Controller for the market view. Wires the search field to live filtering
 * and the table row click to opening a stock detail view.
 */
public class MarketController {

  private static final int TOP_LIST_LIMIT = 5;

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

    view.setOnUpdate(this::applyFilter);
    wireSearch();
    wireRowClick();
    applyFilter();
  }

  public MarketView getView() {
    return view;
  }

  private void wireSearch() {
    view.getSearchField().textProperty()
        .addListener((obs, oldValue, newValue) -> applyFilter());
  }

  private void applyFilter() {
    String query = view.getSearchField().getText();
    List<Stock> all = session.getExchange().getStocks();
    List<Stock> shown = (query == null || query.isBlank())
        ? all
        : session.getExchange().findStocks(query);
    view.setStocks(shown);
    view.setCount("Showing " + shown.size() + " of " + all.size() + " stocks");
    view.setGainers(session.getExchange().getGainers(TOP_LIST_LIMIT));
    view.setLosers(session.getExchange().getLosers(TOP_LIST_LIMIT));
  }

  private void wireRowClick() {
    view.getStockTable().setRowFactory(tv -> {
      TableRow<Stock> row = new TableRow<>();
      row.setOnMouseClicked(e -> {
        if (e.getClickCount() == 2 && !row.isEmpty()) {
          mainController.showStockDetail(row.getItem());
        }
      });
      return row;
    });
  }
}

