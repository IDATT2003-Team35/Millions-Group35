package edu.ntnu.idi.idatt.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** View for the first screen where the user chooses how to start the game. */
public class StartMenuView {
  private final StackPane root;
  private final Button newGameButton;
  private final Button loadGameButton;

  /** Creates the start menu layout and initializes its controls. */
  public StartMenuView() {
    Label titleLabel = new Label("MILLIONS");
    titleLabel.getStyleClass().add("start-title");
    Label subtitleLabel = new Label("Stock Trading Simulator");
    subtitleLabel.getStyleClass().add("start-subtitle");

    VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
    titleBox.getStyleClass().add("start-card-header");
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setMaxWidth(Double.MAX_VALUE);

    newGameButton = new Button("NEW GAME");
    newGameButton.getStyleClass().add("start-primary-button");
    newGameButton.setDefaultButton(true);
    loadGameButton = new Button("LOAD GAME");
    loadGameButton.getStyleClass().add("start-primary-button");

    VBox buttonBox = new VBox(12, newGameButton, loadGameButton);
    buttonBox.getStyleClass().addAll("start-card-body", "start-menu-body");
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setMaxHeight(Double.MAX_VALUE);

    VBox menuBox = new VBox(titleBox, buttonBox);
    menuBox.getStyleClass().add("start-card");
    menuBox.setAlignment(Pos.CENTER);
    menuBox.setPrefWidth(720);
    menuBox.setPrefHeight(520);
    menuBox.setMaxWidth(720);
    menuBox.setMaxHeight(520);
    VBox.setVgrow(buttonBox, Priority.ALWAYS);

    root = new StackPane(menuBox);
    root.getStyleClass().add("start-root");
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
