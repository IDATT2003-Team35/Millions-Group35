package edu.ntnu.idi.idatt.millions.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Top status bar showing player info, net worth, level, week and advance week button.
 */
public class StatusBar extends HBox {

  private final Label placeholder = new Label("StatusBar");

  public StatusBar() {
    getChildren().add(placeholder);
  }
}
