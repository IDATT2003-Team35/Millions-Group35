package edu.ntnu.idi.idatt.millions;

import edu.ntnu.idi.idatt.millions.controller.MainController;
import edu.ntnu.idi.idatt.millions.controller.StartController;
import edu.ntnu.idi.idatt.millions.file.StockReader;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.view.StartView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Millions JavaFX application.
 */
public class App extends Application {
  private Stage primaryStage;

  @Override
  public void start(Stage stage) {
    this.primaryStage = stage;

    StartView startView = new StartView();
    new StartController(startView, stage, this::handleGameStart, new StockReader());

    Scene scene = new Scene(startView.getRoot());
    stage.setTitle("Millions");
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.show();
  }

  private void handleGameStart(GameSession session) {
    MainController controller = new MainController(session);
    Scene scene = new Scene(controller.getView(), primaryStage.getWidth(), primaryStage.getHeight()
    );
    primaryStage.setScene(scene);
  }

  public static void main(String[] args) {
    launch(args);
  }
}