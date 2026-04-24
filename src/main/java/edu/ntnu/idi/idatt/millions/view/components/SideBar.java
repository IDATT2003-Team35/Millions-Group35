package edu.ntnu.idi.idatt.millions.view.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Left side navigation bar. Contains buttons for Market, Portfolio,
 * Transactions and a bottom-anchored "Sell All and Quit". Controllers
 * wire actions to the exposed buttons.
 */
public class SideBar extends VBox {

  private final Button marketButton = new Button("Market");
  private final Button portfolioButton = new Button("Portfolio");
  private final Button transactionButton = new Button("Transactions");
  private final Button sellAllButton = new Button("Sell All and Quit");

  public SideBar() {
    setSpacing(8);
    setPadding(new Insets(10));

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    getChildren().addAll(
        new Label("NAVIGATION"),
        marketButton,
        portfolioButton,
        transactionButton,
        spacer,
        sellAllButton
    );
  }

  public Button getMarketButton() {
    return marketButton;
  }

  public Button getPortfolioButton() {
    return portfolioButton;
  }

  public Button getTransactionButton() {
    return transactionButton;
  }

  public Button getSellAllButton() {
    return sellAllButton;
  }
}
