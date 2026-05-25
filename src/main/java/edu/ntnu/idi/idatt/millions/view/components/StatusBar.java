package edu.ntnu.idi.idatt.millions.view.components;

import edu.ntnu.idi.idatt.millions.model.Difficulty;
import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.GameMode;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import java.math.BigDecimal;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Top status bar showing compact game state and the advance-week button. Values are updated via
 * {@link #refresh}.
 */
public class StatusBar extends HBox {

  private final Label cashValue = new Label();
  private final Label netWorthValue = new Label();
  private final Label changeValue = new Label();
  private final Label weekValue = new Label();
  private final ProgressBar weekProgress = new ProgressBar(0);
  private final Label difficultyValue = new Label();
  private final Button advanceButton = new Button("ADVANCE WEEK  →");

  /** Creates the status bar layout and initializes its labels and advance button. */
  public StatusBar() {
    getStyleClass().add("status-bar");
    advanceButton.getStyleClass().add("advance-button");

    weekProgress.getStyleClass().add("week-progress");
    weekProgress.setPrefWidth(120);
    weekProgress.setVisible(false);
    weekProgress.setManaged(false);

    HBox stats =
        new HBox(
            field("CASH", cashValue),
            field("NET WORTH", compact(netWorthValue, changeValue)),
            field("WEEK", compactVertical(weekValue, weekProgress)),
            field("DIFFICULTY", difficultyValue));
    stats.getStyleClass().add("status-stats");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    getChildren().addAll(buildLogo(), stats, spacer, advanceButton);
  }

  /**
   * Updates all value labels from the given session.
   *
   * @param session the active game session
   */
  public void refresh(GameSession session) {
    Player player = session.getPlayer();
    Exchange exchange = session.getExchange();

    cashValue.setText(Money.format(player.getMoney()));
    netWorthValue.setText(Money.format(player.getNetWorth()));

    BigDecimal pct = player.getTotalGainLossPercent();
    changeValue.setText(Percentages.formatWithArrow(pct));
    changeValue.getStyleClass().removeAll("gain", "loss");
    if (pct.signum() > 0) {
      changeValue.getStyleClass().add("gain");
    } else if (pct.signum() < 0) {
      changeValue.getStyleClass().add("loss");
    }

    refreshWeek(exchange, session.getMode());
    refreshDifficulty(session.getDifficulty());
  }

  /** Updates the week label and progress bar based on the active mode. */
  private void refreshWeek(Exchange exchange, GameMode mode) {
    int week = exchange.getWeek();
    if (mode.hasWeekLimit()) {
      int limit = mode.getWeekLimit();
      weekValue.setText(String.format("%02d / %d", week, limit));
      weekProgress.setProgress(Math.min(1.0, (double) week / limit));
      weekProgress.setVisible(true);
      weekProgress.setManaged(true);
    } else {
      weekValue.setText(String.format("%02d", week));
      weekProgress.setVisible(false);
      weekProgress.setManaged(false);
    }
  }

  /** Updates the difficulty label with text and color class. */
  private void refreshDifficulty(Difficulty difficulty) {
    difficultyValue.setText(formatDifficulty(difficulty));
    difficultyValue
        .getStyleClass()
        .removeAll("difficulty-easy", "difficulty-normal", "difficulty-hard");
    switch (difficulty) {
      case EASY -> difficultyValue.getStyleClass().add("difficulty-easy");
      case NORMAL -> difficultyValue.getStyleClass().add("difficulty-normal");
      case HARD -> difficultyValue.getStyleClass().add("difficulty-hard");
    }
  }

  private String formatDifficulty(Difficulty difficulty) {
    String name = difficulty.name();
    return name.charAt(0) + name.substring(1).toLowerCase();
  }

  /**
   * Returns the button used to advance the game by one week.
   *
   * @return the advance week button
   */
  public Button getAdvanceButton() {
    return advanceButton;
  }

  private VBox buildLogo() {
    Label title = new Label("MILLIONS");
    title.getStyleClass().add("logo-title");
    Label subtitle = new Label("MARKETS");
    subtitle.getStyleClass().add("logo-subtitle");

    VBox textContent = new VBox(title, subtitle);
    textContent.setPadding(new Insets(10, 18, 6, 18));

    Region blueBlock = new Region();
    blueBlock.getStyleClass().add("logo-stripe");
    blueBlock.setPrefHeight(10);

    VBox box = new VBox(textContent, blueBlock);
    box.getStyleClass().add("logo-block");
    return box;
  }

  private VBox field(String title, Node valueNode) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("field-title");
    if (valueNode instanceof Label valueLabel) {
      valueLabel.getStyleClass().add("field-value");
    }
    VBox box = new VBox(titleLabel, valueNode);
    box.getStyleClass().add("status-field");
    return box;
  }

  private HBox compact(Label leftLabel, Label rightLabel) {
    leftLabel.getStyleClass().add("field-value");
    rightLabel.getStyleClass().addAll("field-value", "status-change-value");
    HBox box = new HBox(leftLabel, rightLabel);
    box.setSpacing(8);
    box.setAlignment(Pos.BOTTOM_LEFT);
    return box;
  }

  private VBox compactVertical(Label label, ProgressBar bar) {
    label.getStyleClass().add("field-value");
    VBox box = new VBox(label, bar);
    box.setSpacing(4);
    box.setAlignment(Pos.BOTTOM_LEFT);
    return box;
  }
}
