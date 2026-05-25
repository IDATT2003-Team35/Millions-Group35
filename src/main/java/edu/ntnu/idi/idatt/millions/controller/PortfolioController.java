package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.*;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import edu.ntnu.idi.idatt.millions.view.PortfolioView;
import edu.ntnu.idi.idatt.millions.view.SellView;
import java.math.BigDecimal;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/** Controller for the portfolio view. */
public class PortfolioController {

  private final GameSession session;
  private final MainController mainController;
  private final PortfolioView view;

  /**
   * Creates a new portfolio controller and the view it manages.
   *
   * @param session the active game session; must not be null
   * @param mainController the parent controller used for navigation must not be null
   * @throws IllegalArgumentException if session or mainController is null
   */
  public PortfolioController(GameSession session, MainController mainController) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    if (mainController == null) {
      throw new IllegalArgumentException("MainController cannot be null");
    }
    this.session = session;
    this.mainController = mainController;
    this.view = new PortfolioView(session);

    view.setOnUpdate(this::applyRefresh);
    wireSellHandler();
    applyRefresh();
  }

  /**
   * Returns the portfolio view managed by this controller.
   *
   * @return the portfolio view
   */
  public PortfolioView getView() {
    return view;
  }

  private void wireSellHandler() {
    view.setSellHandler(this::showSellPopup);
  }

  private void applyRefresh() {
    Player player = session.getPlayer();
    Portfolio portfolio = player.getPortfolio();

    view.setHoldingsCount(portfolio.getHoldings().size());
    view.setStockValue(Money.format(portfolio.getNetWorth()));
    BigDecimal totalGainLoss = player.getNetWorth().subtract(player.getStartingMoney());
    view.setTotalGainLoss(Money.formatWithSign(totalGainLoss));
    view.setTotalGainLossPercent(Percentages.format(player.getTotalGainLossPercent()));
    view.setNetWorthHistory(session.getNetWorthHistory());
    view.setHoldings(portfolio.getHoldings());
  }

  private void showSellPopup(PortfolioHolding holding) {
    if (holding == null) {
      return;
    }

    SellView sellView = new SellView();

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(view.getScene().getWindow());
    dialogStage.initStyle(StageStyle.UNDECORATED);
    dialogStage.setTitle("Sell Order");
    dialogStage.setScene(new Scene(sellView.getRoot()));

    new SellController(sellView, dialogStage, session, holding);
    dialogStage.showAndWait();
  }
}
