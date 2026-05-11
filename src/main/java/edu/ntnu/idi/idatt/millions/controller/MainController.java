package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.MainView;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Root controller for the main screen. Wires status bar and navigation events
 * to the game session; the view itself observes the session for display updates.
 */
public class MainController {

  private final GameSession session;
  private final MainView view;

  private final Node marketContent;
  private final Node portfolioContent;
  private final Node transactionContent = placeholder("Transactions");
  private final StockDetailController stockDetailController;
  /**
   * Creates the main controller and the main view it manages.
   *
   * @param session the active game session; must not be null
   * @throws IllegalArgumentException if session is null
   */
  public MainController(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    this.session = session;
    this.view = new MainView(session);

    this.marketContent = new MarketController(session, this).getView();
    this.portfolioContent = new PortfolioController(session, this).getView();
    this.stockDetailController = new StockDetailController(session, this);

    wireStatusBar();
    wireNavigation();
    showMarket();
  }

  public MainView getView() {
    return view;
  }

  private void wireStatusBar() {
    view.getStatusBar().getAdvanceButton()
        .setOnAction(e -> session.advanceWeek());
  }

  private void wireNavigation() {
    view.getSideBar().getMarketButton().setOnAction(e -> showMarket());
    view.getSideBar().getPortfolioButton().setOnAction(e -> showPortfolio());
    view.getSideBar().getTransactionButton()
        .setOnAction(e -> view.showContent(transactionContent, view.getSideBar().getTransactionButton()));
  }

  /**
   * Shows the market view in the center area and marks the Market button active.
   */
  public void showMarket() {
    view.showContent(marketContent, view.getSideBar().getMarketButton());
  }

  /**
   * Shows the portfolio view in the center area and marks the Portfolio button active.
   */
  public void showPortfolio() {
    view.showContent(portfolioContent, view.getSideBar().getPortfolioButton());
  }

  public void showStockDetail(Stock stock) {
    stockDetailController.display(stock);
    view.showContent(stockDetailController.getView(),
        view.getSideBar().getMarketButton());
  }

  private static Node placeholder(String title) {
    VBox box = new VBox(new Label(title + " (placeholder)"));
    box.setPadding(new Insets(10));
    return box;
  }
}
