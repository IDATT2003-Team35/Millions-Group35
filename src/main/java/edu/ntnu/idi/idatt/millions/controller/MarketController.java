package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.MarketView;
import java.math.BigDecimal;
import java.util.List;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Controller for the market view. Wires the search field to live filtering and the table row click
 * to opening a stock detail view.
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
    view.setOnStockClick(mainController::showStockDetail);
    wireSearch();
    wireRowClick();
    applyFilter();
  }

  public MarketView getView() {
    return view;
  }

  private void wireSearch() {
    view.getSearchField().textProperty().addListener((obs, oldValue, newValue) -> applyFilter());
  }

  private void applyFilter() {
    String query = view.getSearchField().getText();
    List<Stock> all = session.getExchange().getStocks();
    List<Stock> searched =
        (query == null || query.isBlank()) ? all : session.getExchange().findStocks(query);

    int gainerCount =
        (int)
            all.stream()
                .filter(s -> s.getLatestPercentChange().compareTo(BigDecimal.ZERO) > 0)
                .count();
    int loserCount =
        (int)
            all.stream()
                .filter(s -> s.getLatestPercentChange().compareTo(BigDecimal.ZERO) < 0)
                .count();
    view.setFilterCounts(all.size(), gainerCount, loserCount);

    String filter = view.getSelectedFilter();
    List<Stock> shown =
        switch (filter) {
          case "GAINERS" ->
              searched.stream()
                  .filter(s -> s.getLatestPercentChange().compareTo(BigDecimal.ZERO) > 0)
                  .toList();
          case "LOSERS" ->
              searched.stream()
                  .filter(s -> s.getLatestPercentChange().compareTo(BigDecimal.ZERO) < 0)
                  .toList();
          default -> searched;
        };

    view.setStocks(shown);
    view.setInstrumentCount(all.size());
    view.setGainers(session.getExchange().getGainers(TOP_LIST_LIMIT));
    view.setLosers(session.getExchange().getLosers(TOP_LIST_LIMIT));
  }

  private void wireRowClick() {
    view.getStockTable()
        .setRowFactory(
            tv -> {
              TableRow<Stock> row = new TableRow<>();
              row.addEventFilter(
                  MouseEvent.MOUSE_PRESSED,
                  e -> {
                    if (e.getButton() == MouseButton.PRIMARY && !row.isEmpty()) {
                      e.consume();
                      view.getStockTable().getSelectionModel().clearSelection();
                      view.getStockTable().getFocusModel().focus(-1);
                      mainController.showStockDetail(row.getItem());
                    }
                  });
              return row;
            });
  }
}
