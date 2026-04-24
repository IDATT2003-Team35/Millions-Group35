package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
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

  private final Node marketContent = placeholder("Market");
  private final Node portfolioContent = placeholder("Portfolio");
  private final Node transactionContent = placeholder("Transactions");

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

    wireStatusBar();
    wireNavigation();
    view.showContent(marketContent, view.getSideBar().getMarketButton());
  }

  public MainView getView() {
    return view;
  }

  private void wireStatusBar() {
    view.getStatusBar().getAdvanceButton()
        .setOnAction(e -> session.advanceWeek());
  }

  private void wireNavigation() {
    view.getSideBar().getMarketButton()
        .setOnAction(e -> view.showContent(marketContent, view.getSideBar().getMarketButton()));
    view.getSideBar().getPortfolioButton()
        .setOnAction(e -> view.showContent(portfolioContent, view.getSideBar().getPortfolioButton()));
    view.getSideBar().getTransactionButton()
        .setOnAction(e -> view.showContent(transactionContent, view.getSideBar().getTransactionButton()));
  }

  private static Node placeholder(String title) {
    VBox box = new VBox(new Label(title + " (placeholder)"));
    box.setPadding(new Insets(10));
    return box;
  }
}
