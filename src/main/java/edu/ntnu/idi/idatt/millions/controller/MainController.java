package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.file.save.GameSaveException;
import edu.ntnu.idi.idatt.millions.file.save.GameSaveService;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import java.math.BigDecimal;
import edu.ntnu.idi.idatt.millions.view.GameOverView;
import edu.ntnu.idi.idatt.millions.view.MainView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Root controller for the main screen. Wires status bar and navigation events
 * to the game session; the view itself observes the session for display updates.
 */
public class MainController {

  private final GameSession session;
  private final MainView view;
  private final GameSaveService saveService;

  private final Node marketContent;
  private final Node portfolioContent;
  private final Node transactionContent;
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
    this.saveService = createSaveService();

    this.marketContent = new MarketController(session, this).getView();
    this.portfolioContent = new PortfolioController(session, this).getView();
    this.stockDetailController = new StockDetailController(session, this);
    this.transactionContent = new TransactionController(session, this).getView();
    wireStatusBar();
    wireNavigation();
    wireSave();
    wireSellAll();
    showMarket();
  }

  public MainView getView() {
    return view;
  }

  private void wireStatusBar() {
    view.getStatusBar().getAdvanceButton()
        .setOnAction(e -> handleAdvance());
  }

  /**
   * Advances one week and shows the game-over screen if the session has
   * reached its end condition (Challenge mode hitting the week limit).
   */
  private void handleAdvance() {
    if (session.isGameOver()) {
      showEndGame();
      return;
    }
    session.advanceWeek();
    if (session.isGameOver()) {
      showEndGame();
    }
  }

  private void wireNavigation() {
    view.getSideBar().getMarketButton().setOnAction(e -> showMarket());
    view.getSideBar().getPortfolioButton().setOnAction(e -> showPortfolio());
    view.getSideBar().getTransactionButton().setOnAction(e -> showTransaction());
  }

  private void wireSave() {
    view.getSideBar().getSaveButton().setOnAction(e -> handleSaveGame());
  }

  private void wireSellAll() {
    view.getSideBar().getSellAllButton().setOnAction(e -> handleSellAllAndQuit());
  }

  private GameSaveService createSaveService() {
    try {
      return new GameSaveService();
    } catch (GameSaveException e) {
      throw new IllegalStateException("Could not initialize save service", e);
    }
  }

  private void handleSaveGame() {
    try {
      Path savePath = saveService.save(session);
      Alert success = new Alert(Alert.AlertType.INFORMATION);
      success.setHeaderText("Game saved");
      success.setContentText("Saved to: " + savePath.getFileName());
      success.showAndWait();
    } catch (GameSaveException e) {
      Alert error = new Alert(Alert.AlertType.ERROR);
      error.setHeaderText("Could not save game");
      error.setContentText(e.getMessage());
      error.showAndWait();
    }
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

  private void handleSellAllAndQuit() {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
        "Sell all holdings and end the game?",
        ButtonType.YES, ButtonType.NO);
    confirm.setHeaderText(null);
    confirm.showAndWait()
        .filter(b -> b == ButtonType.YES)
        .ifPresent(b -> sellAllAndEndGame());
  }

  private void sellAllAndEndGame() {
    List<Share> sharesToSell = new ArrayList<>(
        session.getPlayer().getPortfolio().getShares());
    for (Share share : sharesToSell) {
      session.sellShare(share);
    }
    showEndGame();
  }

  private void showEndGame() {
    GameOverView gameOverView = new GameOverView();
    Player player = session.getPlayer();
    BigDecimal gainPercent = player.getTotalGainLossPercent();

    gameOverView.setPlayerName(player.getName());
    gameOverView.setNetWorth(Money.format(player.getNetWorth()));
    gameOverView.setStartingCapital(Money.format(player.getStartingMoney()));
    gameOverView.setGainLossPercentValue(Percentages.format(gainPercent));
    gameOverView.setHeadlineSignum(gainPercent.signum());
    gameOverView.setRank(player.getStatus().toString());
    gameOverView.setWeeks(String.valueOf(session.getExchange().getWeek()));
    gameOverView.setTransactions(
        String.valueOf(player.getTransactionArchive().getAll().size()));
    gameOverView.getQuitButton().setOnAction(e -> Platform.exit());

    Scene scene = view.getScene();
    scene.setRoot(gameOverView.getRoot());
  }

  public void showStockDetail(Stock stock) {
    stockDetailController.display(stock);
    view.showContent(stockDetailController.getView(),
        view.getSideBar().getMarketButton());
  }

  public void showTransaction() {
    view.showContent(transactionContent,
        view.getSideBar().getTransactionButton());
  }
}
