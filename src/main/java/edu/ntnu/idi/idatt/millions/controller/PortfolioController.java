package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.view.PortfolioView;
import edu.ntnu.idi.idatt.millions.view.SellView;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controller for the portfolio view.
 */
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

    wireSellHandler();
  }

  public PortfolioView getView() {
    return view;
  }

  private void wireSellHandler() {
    view.setSellHandler(this::showSellPopup);
  }

  private void showSellPopup(Share share) {
    if (share == null) {
      return;
    }

    SellView sellView = new SellView();

    Stage dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.initOwner(view.getScene().getWindow());
    dialogStage.initStyle(StageStyle.UNDECORATED);
    dialogStage.setTitle("Sell Order");
    dialogStage.setScene(new Scene(sellView.getRoot()));

    new SellController(sellView, dialogStage, session, share);
    dialogStage.showAndWait();
  }
}
