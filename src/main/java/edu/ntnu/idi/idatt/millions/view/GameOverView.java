package edu.ntnu.idi.idatt.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * View shown when the player ends the game. Displays a summary of the
 * final session state and a quit button.
 */
public class GameOverView {
  private final StackPane root;
  private final Label playerNameValue = new Label();
  private final Label netWorthValue = new Label();
  private final Label startingCapitalValue = new Label();
  private final Label gainLossPercentValue = new Label();
  private final Label rankValue = new Label();
  private final Label weeksValue = new Label();
  private final Label transactionsValue = new Label();
  private final Button quitButton = new Button("Quit");

  /**
   * Creates the game over screen layout.
   */
  public GameOverView() {
    Label titleLabel = new Label("GAME OVER");
    Label subtitleLabel = new Label("Final Summary");

    VBox titleBox = new VBox(10, titleLabel, subtitleLabel);
    titleBox.setAlignment(Pos.CENTER);
    titleBox.setPadding(new Insets(30, 20, 30, 20));

    VBox statsBox = new VBox(
        10,
        row("Player", playerNameValue),
        row("Final Net Worth ($)", netWorthValue),
        row("Starting capital ($)", startingCapitalValue),
        row("Percentage change", gainLossPercentValue),
        row("Final Rank", rankValue),
        row("Weeks Played", weeksValue),
        row("Transactions Completed", transactionsValue)
    );

    quitButton.setDefaultButton(true);

    VBox card = new VBox(
        titleBox,
        statsBox,
        new Separator(),
        quitButton
    );
    card.setSpacing(15);
    card.setAlignment(Pos.CENTER);
    card.setMaxWidth(720);
    card.setPadding(new Insets(20));

    root = new StackPane(card);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
  }

  private static VBox row(String labelText, Label valueLabel) {
    VBox box = new VBox(4, new Label(labelText), valueLabel);
    box.setAlignment(Pos.CENTER);
    return box;
  }

  public Parent getRoot() {
    return root;
  }

  public Button getQuitButton() {
    return quitButton;
  }

  public void setPlayerName(String name) {
    playerNameValue.setText(name);
  }

  public void setNetWorth(String netWorth) {
    netWorthValue.setText(netWorth);
  }

  public void setStartingCapital(String startingCapital) {
    startingCapitalValue.setText(startingCapital);
  }

  public void setGainLossPercentValue(String percentValue) {
    gainLossPercentValue.setText(percentValue);
  }

  public void setRank(String rank) {
    rankValue.setText(rank);
  }

  public void setWeeks(String weeks) {
    weeksValue.setText(weeks);
  }

  public void setTransactions(String transactions) {
    transactionsValue.setText(transactions);
  }
}