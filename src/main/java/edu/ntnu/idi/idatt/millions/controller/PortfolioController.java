package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.view.PortfolioView;

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
  }

  public PortfolioView getView() {
    return view;
  }
}
