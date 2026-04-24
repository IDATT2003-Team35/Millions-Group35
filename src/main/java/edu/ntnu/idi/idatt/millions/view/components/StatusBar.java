package edu.ntnu.idi.idatt.millions.view.components;

import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.Player;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Top status bar showing player info, net worth, level, week
 * and the advance-week button. Values are updated via {@link #refresh}.
 */
public class StatusBar extends HBox {

  private final Label nameValue = new Label();
  private final Label cashValue = new Label();
  private final Label netWorthValue = new Label();
  private final Label levelValue = new Label();
  private final Label weekValue = new Label();
  private final Button advanceButton = new Button("Advance Week");

  public StatusBar() {
    setSpacing(24);
    setPadding(new Insets(10));
    getChildren().addAll(
        field("Name", nameValue),
        field("Cash", cashValue),
        field("Net Worth", netWorthValue),
        field("Level", levelValue),
        field("Week", weekValue),
        advanceButton
    );
  }

  /**
   * Updates all value labels from the given player and exchange.
   *
   * @param player the active player
   * @param exchange the active exchange
   */
  public void refresh(Player player, Exchange exchange) {
    nameValue.setText(player.getName());
    cashValue.setText("$" + player.getMoney());
    netWorthValue.setText("$" + player.getNetWorth());
    levelValue.setText(player.getStatus().toString());
    weekValue.setText(String.valueOf(exchange.getWeek()));
  }

  public Button getAdvanceButton() {
    return advanceButton;
  }

  private VBox field(String title, Label valueLabel) {
    VBox box = new VBox(new Label(title), valueLabel);
    box.setSpacing(2);
    return box;
  }
}
