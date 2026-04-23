package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.view.MainView;

/**
 * Root controller for the main screen. Owns the {@link GameSession},
 * observes it, and coordinates navigation and the status bar.
 */
public class MainController implements Observer {

  private final GameSession session;
  private final MainView view;

  /**
   * Creates the main controller and registers it as an observer on the session.
   *
   * @param session the active game session; must not be null
   * @throws IllegalArgumentException if session is null
   */
  public MainController(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    this.session = session;
    this.view = new MainView();
    session.addObserver(this);
  }

  public MainView getView() {
    return view;
  }

  @Override
  public void update() {
  }
}
