package edu.ntnu.idi.idatt.millions.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Left-side navigation bar with buttons for Market, Portfolio, and Transactions.
 */
public class SideBar extends VBox {

  private final Label placeholder = new Label("SideBar");

  public SideBar() {
    getChildren().add(placeholder);
  }
}
