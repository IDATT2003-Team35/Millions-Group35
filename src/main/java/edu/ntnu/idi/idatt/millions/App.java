package edu.ntnu.idi.idatt.millions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Entry point for the Millions JavaFX application.
 */
public class App extends Application {

  @Override
  public void start(Stage stage) {
    Label label = new Label("Millions Stock Trading Game");
    StackPane root = new StackPane(label);
    Scene scene = new Scene(root, 800, 600);
    stage.setTitle("Millions");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

