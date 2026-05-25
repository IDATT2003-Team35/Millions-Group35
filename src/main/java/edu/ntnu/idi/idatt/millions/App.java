package edu.ntnu.idi.idatt.millions;

import edu.ntnu.idi.idatt.millions.controller.LoadGameController;
import edu.ntnu.idi.idatt.millions.controller.MainController;
import edu.ntnu.idi.idatt.millions.controller.StartController;
import edu.ntnu.idi.idatt.millions.controller.StartMenuController;
import edu.ntnu.idi.idatt.millions.file.StockReader;
import edu.ntnu.idi.idatt.millions.file.save.GameSaveException;
import edu.ntnu.idi.idatt.millions.file.save.GameSaveService;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.util.Styles;
import edu.ntnu.idi.idatt.millions.view.LoadGameView;
import edu.ntnu.idi.idatt.millions.view.StartMenuView;
import edu.ntnu.idi.idatt.millions.view.StartView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Entry point for the Millions JavaFX application. */
public class App extends Application {
  private Stage primaryStage;

  /** Creates the JavaFX application instance. */
  public App() {}

  @Override
  public void start(Stage stage) {
    this.primaryStage = stage;

    Scene scene = new Scene(new StackPane());
    Styles.applyTo(scene);
    stage.setTitle("Millions");
    stage.setScene(scene);
    stage.setMaximized(true);
    showStartMenu();
    stage.show();
  }

  private void showStartMenu() {
    StartMenuView startMenuView = new StartMenuView();
    new StartMenuController(startMenuView, this::showNewGameSetup, this::showLoadGame);
    primaryStage.getScene().setRoot(startMenuView.getRoot());
  }

  private void showNewGameSetup() {
    StartView startView = new StartView();
    new StartController(
        startView, primaryStage, this::handleGameStart, new StockReader(), this::showStartMenu);
    primaryStage.getScene().setRoot(startView.getRoot());
  }

  private void showLoadGame() {
    LoadGameView loadGameView = new LoadGameView();
    try {
      new LoadGameController(
          loadGameView, new GameSaveService(), this::handleGameStart, this::showStartMenu);
    } catch (GameSaveException e) {
      loadGameView.setErrorMessage(e.getMessage());
    }
    primaryStage.getScene().setRoot(loadGameView.getRoot());
  }

  private void handleGameStart(GameSession session) {
    MainController controller = new MainController(session);
    primaryStage.getScene().setRoot(controller.getView());
  }

  /**
   * Launches the JavaFX application.
   *
   * @param args command line arguments passed to JavaFX
   */
  public static void main(String[] args) {
    launch(args);
  }
}
