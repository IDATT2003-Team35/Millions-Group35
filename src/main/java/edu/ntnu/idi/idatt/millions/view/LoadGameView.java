package edu.ntnu.idi.idatt.millions.view;

import java.nio.file.Path;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** View for selecting and loading a saved game. */
public class LoadGameView {
  private final StackPane root;
  private final ListView<Path> saveListView;
  private final Button loadButton;
  private final Button backButton;
  private final Label errorLabel;

  /** Creates the load game layout and initializes its controls. */
  public LoadGameView() {
    Label titleLabel = new Label("LOAD GAME");
    titleLabel.getStyleClass().add("start-title");
    Label subtitleLabel = new Label("Choose a saved game");
    subtitleLabel.getStyleClass().add("start-subtitle");

    VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
    titleBox.getStyleClass().add("start-card-header");
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setMaxWidth(Double.MAX_VALUE);

    saveListView = new ListView<>();
    saveListView.setCellFactory(
        listView ->
            new ListCell<>() {
              @Override
              protected void updateItem(Path path, boolean empty) {
                super.updateItem(path, empty);
                setText(empty || path == null ? null : path.getFileName().toString());
              }
            });
    VBox.setVgrow(saveListView, Priority.ALWAYS);

    errorLabel = new Label();
    errorLabel.getStyleClass().add("start-error");
    errorLabel.setWrapText(true);

    backButton = new Button("BACK");
    backButton.getStyleClass().add("start-secondary-button");
    loadButton = new Button("LOAD");
    loadButton.getStyleClass().add("start-primary-button");
    loadButton.setDefaultButton(true);

    HBox buttonBox = new HBox(12, backButton, loadButton);
    buttonBox.setAlignment(Pos.CENTER);

    VBox contentBox = new VBox(14, saveListView, errorLabel, buttonBox);
    contentBox.getStyleClass().add("start-card-body");
    contentBox.setAlignment(Pos.CENTER);

    VBox card = new VBox(titleBox, contentBox);
    card.getStyleClass().add("start-card");
    card.setAlignment(Pos.CENTER);
    card.setPrefWidth(720);
    card.setMaxWidth(720);
    card.setMaxHeight(520);

    root = new StackPane(card);
    root.getStyleClass().add("start-root");
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
  }

  /**
   * Returns the root node of the load game view.
   *
   * @return the root node
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Returns the list view containing available save files.
   *
   * @return the save file list view
   */
  public ListView<Path> getSaveListView() {
    return saveListView;
  }

  /**
   * Returns the load button.
   *
   * @return the load button
   */
  public Button getLoadButton() {
    return loadButton;
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
   * Updates the save list.
   *
   * @param saveFiles the save files to display
   */
  public void setSaveFiles(List<Path> saveFiles) {
    saveListView.getItems().setAll(saveFiles);
  }

  /**
   * Displays an error message.
   *
   * @param message the message to display
   */
  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  /** Clears the displayed error message. */
  public void clearErrorMessage() {
    errorLabel.setText("");
  }
}
