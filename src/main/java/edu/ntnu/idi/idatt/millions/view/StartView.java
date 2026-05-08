package edu.ntnu.idi.idatt.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
  private final Button startButton;
  private final Label errorLabel;

  /**
   * Creates the start screen layout and initializes its controls.
   */
  public StartView() {
    Label titleLabel = new Label("MILLIONS");
    Label subtitleLabel = new Label("Stock Trading Simulator");

    VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setPadding(new Insets(30, 20, 30, 20));

    Label nameLabel = new Label("Player Name");
    nameField = new TextField();
    nameField.setPromptText("Enter player name");

    Label capitalLabel = new Label("Starting Capital ($)");
    capitalField = new TextField();
    capitalField.setPromptText("Enter starting capital");

    Label fileLabel = new Label("Stock Data File (.csv)");
    fileField = new TextField();
    fileField.setPromptText("Choose a CSV file");
    fileField.setEditable(false);
    fileField.setFocusTraversable(false);

    browseButton = new Button("Browse");

    HBox fileBox = new HBox(10, fileField, browseButton);
    fileBox.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(fileField, Priority.ALWAYS);

    errorLabel = new Label();
    errorLabel.setWrapText(true);

    startButton = new Button("START");
    startButton.setDefaultButton(true);

    HBox startBox = new HBox(10, startButton);
    startBox.setAlignment(Pos.CENTER);

    VBox inputBox = new VBox(
            10,
            nameLabel,
            nameField,
            capitalLabel,
            capitalField,
            fileLabel,
            fileBox,
            new Separator(),
            errorLabel,
            startBox
    );

    VBox card = new VBox(titleBox, inputBox);
    card.setMaxWidth(720);

    root = new StackPane(card);
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
   * Returns the start button used to begin the game.
   *
   * @return the start button
   */
  public Button getStartButton() {
   return startButton;
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
}
