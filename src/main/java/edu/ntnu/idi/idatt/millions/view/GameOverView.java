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
 * View shown when the player ends the game. Displays a summary of the final session state and a
 * quit button.
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
  private final Button quitButton = new Button("QUIT");

  /** Creates the game over screen layout. */
  public GameOverView() {
    Label titleLabel = new Label("GAME OVER");
    titleLabel.getStyleClass().add("game-over-title");
    Label subtitleLabel = new Label("Final Summary");
    subtitleLabel.getStyleClass().add("game-over-subtitle");

    VBox titleBox = new VBox(8, titleLabel, subtitleLabel);
    titleBox.setAlignment(Pos.CENTER);

    netWorthValue.getStyleClass().add("game-over-headline-value");
    gainLossPercentValue.getStyleClass().add("game-over-headline-percent");

    VBox headlineBox = new VBox(4, netWorthValue, gainLossPercentValue);
    headlineBox.setAlignment(Pos.CENTER);
    headlineBox.getStyleClass().add("game-over-headline");

    VBox statsBox =
        new VBox(
            12,
            row("PLAYER", playerNameValue),
            row("STARTING CAPITAL", startingCapitalValue),
            row("FINAL RANK", rankValue),
            row("WEEKS PLAYED", weeksValue),
            row("TRANSACTIONS COMPLETED", transactionsValue));
    statsBox.getStyleClass().add("game-over-stats");

    quitButton.setDefaultButton(true);
    quitButton.getStyleClass().add("game-over-quit-button");

    VBox card =
        new VBox(20, titleBox, headlineBox, new Separator(), statsBox, new Separator(), quitButton);
    card.getStyleClass().add("game-over-card");
    card.setAlignment(Pos.CENTER);
    card.setMaxWidth(560);
    card.setPadding(new Insets(40, 48, 40, 48));

    root = new StackPane(card);
    root.getStyleClass().add("game-over-root");
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(40));
  }

  private static VBox row(String labelText, Label valueLabel) {
    Label label = new Label(labelText);
    label.getStyleClass().add("game-over-stat-label");
    valueLabel.getStyleClass().add("game-over-stat-value");
    VBox box = new VBox(2, label, valueLabel);
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

  /**
   * Applies a green/red color class to the headline values (net worth + percent) based on the sign
   * of the overall gain.
   *
   * @param signum 1 for gain, -1 for loss, 0 for neutral
   */
  public void setHeadlineSignum(int signum) {
    netWorthValue.getStyleClass().removeAll("gain", "loss");
    gainLossPercentValue.getStyleClass().removeAll("gain", "loss");
    if (signum > 0) {
      netWorthValue.getStyleClass().add("gain");
      gainLossPercentValue.getStyleClass().add("gain");
    } else if (signum < 0) {
      netWorthValue.getStyleClass().add("loss");
      gainLossPercentValue.getStyleClass().add("loss");
    }
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
