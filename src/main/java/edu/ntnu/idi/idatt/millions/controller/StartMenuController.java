package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.view.StartMenuView;

/**
 * Controller for the start menu. Wires the new game and load game choices to app-level navigation
 * callbacks.
 */
public class StartMenuController {
  private final StartMenuView view;
  private final Runnable onNewGame;
  private final Runnable onLoadGame;

  /**
   * Creates a start menu controller and wires its actions.
   *
   * @param view the start menu view
   * @param onNewGame callback invoked when the user chooses a new game
   * @param onLoadGame callback invoked when the user chooses to load a game
   * @throws IllegalArgumentException if any argument is null
   */
  public StartMenuController(StartMenuView view, Runnable onNewGame, Runnable onLoadGame) {
    if (view == null) {
      throw new IllegalArgumentException("view cannot be null");
    }
    if (onNewGame == null) {
      throw new IllegalArgumentException("onNewGame cannot be null");
    }
    if (onLoadGame == null) {
      throw new IllegalArgumentException("onLoadGame cannot be null");
    }

    this.view = view;
    this.onNewGame = onNewGame;
    this.onLoadGame = onLoadGame;

    initialize();
  }

  private void initialize() {
    view.getNewGameButton().setOnAction(e -> onNewGame.run());
    view.getLoadGameButton().setOnAction(e -> onLoadGame.run());
  }
}
