package edu.ntnu.idi.idatt.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View for the first screen where the user chooses how to start the game.
 */
public class StartMenuView {
  private final StackPane root;
  private final Button newGameButton;
  private final Button loadGameButton;

  /**
   * Creates the start menu layout and initializes its controls.
   */
  public StartMenuView() {
    Label titleLabel = new Label("MILLIONS");
    Label subtitleLabel = new Label("Stock Trading Simulator");

    newGameButton = new Button("NEW GAME");
    newGameButton.setDefaultButton(true);
    loadGameButton = new Button("LOAD GAME");

    VBox menuBox = new VBox(
        18,
        titleLabel,
        subtitleLabel,
        newGameButton,
        loadGameButton
    );
    menuBox.setAlignment(Pos.CENTER);
    menuBox.setMaxWidth(420);
    menuBox.setPadding(new Insets(40));

    root = new StackPane(menuBox);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
  }

  /**
   * Returns the root node of the start menu.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Returns the button used to start a new game.
   *
   * @return the new game button
   */
  public Button getNewGameButton() {
    return newGameButton;
  }

  /**
   * Returns the button used to load a saved game.
   *
   * @return the load game button
   */
  public Button getLoadGameButton() {
    return loadGameButton;
  }
}
