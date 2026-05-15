package edu.ntnu.idi.idatt.millions.view.components;

import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import java.math.BigDecimal;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Top status bar showing player info, net worth, level, week
 * and the advance-week button. Values are updated via {@link #refresh}.
 */
public class StatusBar extends HBox {

  private final Label nameValue = new Label();
  private final Label cashValue = new Label();
  private final Label netWorthValue = new Label();
  private final Label changeValue = new Label();
  private final Label levelValue = new Label();
  private final Label weekValue = new Label();
  private final Button advanceButton = new Button("ADVANCE WEEK  →");

  public StatusBar() {
    getStyleClass().add("status-bar");
    advanceButton.getStyleClass().add("advance-button");

    HBox stats = new HBox(
        field("PLAYER", nameValue),
        field("CASH", cashValue),
        field("NET WORTH", compact(netWorthValue, changeValue)),
        field("RANK", levelValue),
        field("WEEK", weekValue)
    );
    stats.getStyleClass().add("status-stats");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    getChildren().addAll(buildLogo(), stats, spacer, advanceButton);
  }

  /**
   * Updates all value labels from the given player and exchange.
   *
   * @param player the active player
   * @param exchange the active exchange
   */
  public void refresh(Player player, Exchange exchange) {
    nameValue.setText(player.getName());
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

    levelValue.setText(player.getStatus().toString());
    weekValue.setText(String.format("%02d", exchange.getWeek()));
  }

  public Button getAdvanceButton() {
    return advanceButton;
  }

  private VBox buildLogo() {
    Label title = new Label("MILLIONS");
    title.getStyleClass().add("logo-title");
    Label subtitle = new Label("MARKETS");
    subtitle.getStyleClass().add("logo-subtitle");

    VBox textContent = new VBox(title, subtitle);
    textContent.setPadding(new Insets(16, 24, 8, 24));

    Region blueBlock = new Region();
    blueBlock.setStyle("-fx-background-color: #2940C5;");
    blueBlock.setPrefHeight(16);

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
    rightLabel.getStyleClass().add("field-value");
    HBox box = new HBox(leftLabel, rightLabel);
    box.setSpacing(8);
    return box;
  }
}
