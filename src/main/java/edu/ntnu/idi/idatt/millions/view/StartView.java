package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.Difficulty;
import edu.ntnu.idi.idatt.millions.model.GameMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View for the start screen where the user enters game setup information.
 * Provides the layout and UI controls used by the start controller.
 */
public class StartView {
  private final StackPane root;
  private final TextField nameField;
  private final TextField capitalField;
  private final TextField fileField;
  private final Button browseButton;
  private final Button defaultStockDataButton;
  private final Button startButton;
  private final Button backButton;
  private final Label errorLabel;
  private final ToggleGroup modeGroup;
  private final ToggleGroup difficultyGroup;
  private final RadioButton sandboxRadio;
  private final RadioButton challengeRadio;
  private final RadioButton easyRadio;
  private final RadioButton normalRadio;
  private final RadioButton hardRadio;

  /**
   * Creates the start screen layout and initializes its controls.
   */
  public StartView() {
    Label titleLabel = new Label("MILLIONS");
    titleLabel.getStyleClass().add("start-title");
    Label subtitleLabel = new Label("Stock Trading Simulator");
    subtitleLabel.getStyleClass().add("start-subtitle");

    VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
    titleBox.getStyleClass().add("start-card-header");
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setMaxWidth(Double.MAX_VALUE);

    Label nameLabel = new Label("Player Name");
    nameLabel.getStyleClass().add("start-form-label");
    nameField = new TextField();
    nameField.setPromptText("Enter player name");

    Label modeLabel = new Label("Game Mode");
    modeLabel.getStyleClass().add("start-form-label");
    modeGroup = new ToggleGroup();
    sandboxRadio = new RadioButton("Sandbox (no time limit)");
    sandboxRadio.setUserData(GameMode.SANDBOX);
    sandboxRadio.setToggleGroup(modeGroup);
    sandboxRadio.setSelected(true);
    challengeRadio = new RadioButton("Challenge (52 weeks)");
    challengeRadio.setUserData(GameMode.CHALLENGE);
    challengeRadio.setToggleGroup(modeGroup);
    HBox modeBox = new HBox(20, sandboxRadio, challengeRadio);
    modeBox.setAlignment(Pos.CENTER_LEFT);

    Label difficultyLabel = new Label("Difficulty");
    difficultyLabel.getStyleClass().add("start-form-label");
    difficultyGroup = new ToggleGroup();
    easyRadio = new RadioButton("Easy");
    easyRadio.setUserData(Difficulty.EASY);
    easyRadio.getStyleClass().add("difficulty-easy");
    easyRadio.setToggleGroup(difficultyGroup);
    normalRadio = new RadioButton("Normal");
    normalRadio.setUserData(Difficulty.NORMAL);
    normalRadio.getStyleClass().add("difficulty-normal");
    normalRadio.setToggleGroup(difficultyGroup);
    normalRadio.setSelected(true);
    hardRadio = new RadioButton("Hard");
    hardRadio.setUserData(Difficulty.HARD);
    hardRadio.getStyleClass().add("difficulty-hard");
    hardRadio.setToggleGroup(difficultyGroup);
    HBox difficultyBox = new HBox(20, easyRadio, normalRadio, hardRadio);
    difficultyBox.setAlignment(Pos.CENTER_LEFT);

    Label capitalLabel = new Label("Starting Capital ($)");
    capitalLabel.getStyleClass().add("start-form-label");
    capitalField = new TextField();
    capitalField.setPromptText("Enter starting capital");

    Label fileLabel = new Label("Stock Data File (.csv)");
    fileLabel.getStyleClass().add("start-form-label");
    fileField = new TextField();
    fileField.setPromptText("Choose a CSV file");
    fileField.setEditable(false);
    fileField.setFocusTraversable(false);

    browseButton = new Button("Browse");
    browseButton.getStyleClass().add("start-secondary-button");
    defaultStockDataButton = new Button("Default stock data");
    defaultStockDataButton.getStyleClass().add("start-secondary-button");

    HBox fileBox = new HBox(10, fileField, browseButton, defaultStockDataButton);
    fileBox.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(fileField, Priority.ALWAYS);

    errorLabel = new Label();
    errorLabel.getStyleClass().add("start-error");
    errorLabel.setWrapText(true);

    backButton = new Button("BACK");
    backButton.getStyleClass().add("start-secondary-button");
    startButton = new Button("START");
    startButton.getStyleClass().add("start-primary-button");
    startButton.setDefaultButton(true);

    HBox startBox = new HBox(10, backButton, startButton);
    startBox.setAlignment(Pos.CENTER);

    VBox inputBox = new VBox(
            10,
            nameLabel,
            nameField,
            modeLabel,
            modeBox,
            difficultyLabel,
            difficultyBox,
            capitalLabel,
            capitalField,
            fileLabel,
            fileBox,
            new Separator(),
            errorLabel,
            startBox
    );
    inputBox.getStyleClass().add("start-card-body");

    VBox card = new VBox(titleBox, inputBox);
    card.getStyleClass().add("start-card");
    card.setPrefWidth(720);
    card.setMaxWidth(720);
    card.setMaxHeight(Region.USE_PREF_SIZE);

    root = new StackPane(card);
    root.getStyleClass().add("start-root");
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
  }

  /**
   * Returns the root node of the start view.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Returns the text field for the player name.
   *
   * @return the player name field
   */
  public TextField getNameField() {
   return nameField;
  }

  /**
   * Returns the text field for the starting capital.
   *
   * @return the starting capital field
   */
  public TextField getCapitalField() {
   return capitalField;
  }

  /**
   * Returns the read-only text field showing the selected file path.
   *
   * @return the stock file field
   */
  public TextField getFileField() {
   return fileField;
  }

  /**
   * Returns the browse button used to choose a CSV file.
   *
   * @return the browse button
   */
  public Button getBrowseButton() {
   return browseButton;
  }

  /**
   * Returns the button used to select the bundled stock data file.
   *
   * @return the default stock data button
   */
  public Button getDefaultStockDataButton() {
    return defaultStockDataButton;
  }

  /**
   * Returns the start button used to begin the game.
   *
   * @return the start button
   */
  public Button getStartButton() {
   return startButton;
  }

  /**
   * Returns the back button.
   *
   * @return the back button
   */
  public Button getBackButton() {
    return backButton;
  }

  /**
   * Returns the label used to show validation errors.
   *
   * @return the error label
   */
  public Label getErrorLabel() {
    return errorLabel;
  }

  /**
   * Updates the file field with the chosen file path.
   *
   * @param path the selected file path
   */
  public void setSelectedFilePath(String path) {
    fileField.setText(path);
  }

  /**
   * Displays an error message on the start screen.
   *
   * @param message the error message to show
   */
  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  /**
   * Clears any displayed error message.
   */
  public void clearErrorMessage() {
    errorLabel.setText("");
  }

  /**
   * Returns the toggle group controlling the game mode radio buttons.
   *
   * @return the mode toggle group
   */
  public ToggleGroup getModeGroup() {
    return modeGroup;
  }

  /**
   * Returns the toggle group controlling the difficulty radio buttons.
   *
   * @return the difficulty toggle group
   */
  public ToggleGroup getDifficultyGroup() {
    return difficultyGroup;
  }

  /**
   * Returns the currently selected game mode.
   *
   * @return the selected mode, or {@link GameMode#SANDBOX} as fallback
   */
  public GameMode getSelectedMode() {
    Toggle selected = modeGroup.getSelectedToggle();
    return selected != null ? (GameMode) selected.getUserData() : GameMode.SANDBOX;
  }

  /**
   * Returns the currently selected difficulty.
   *
   * @return the selected difficulty, or {@link Difficulty#NORMAL} as fallback
   */
  public Difficulty getSelectedDifficulty() {
    Toggle selected = difficultyGroup.getSelectedToggle();
    return selected != null ? (Difficulty) selected.getUserData() : Difficulty.NORMAL;
  }
}
