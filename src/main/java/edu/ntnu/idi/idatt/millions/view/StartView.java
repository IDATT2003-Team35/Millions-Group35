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

public class StartView {
  private final StackPane root;
  private final TextField nameField;
  private final TextField capitalField;
  private final TextField fileField;
  private final Button browseButton;
  private final Button startButton;
  private final Label errorLabel;

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

  public Parent getRoot() {
    return root;
  }

  public TextField getNameField() {
   return nameField;
  }

  public TextField getCapitalField() {
   return capitalField;
  }

  public TextField getFileField() {
   return fileField;
  }

  public Button getBrowseButton() {
   return browseButton;
  }

  public Button getStartButton() {
   return startButton;
  }

  public Label getErrorLabel() {
    return errorLabel;
  }

  public void setSelectedFilePath(String path) {
    fileField.setText(path);
  }

  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  public void clearErrorMessage() {
    errorLabel.setText("");
  }
}
