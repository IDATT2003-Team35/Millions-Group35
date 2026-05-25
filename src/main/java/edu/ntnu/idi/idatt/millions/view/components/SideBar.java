package edu.ntnu.idi.idatt.millions.view.components;

import edu.ntnu.idi.idatt.millions.model.Player;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Left side navigation bar. Contains buttons for Market, Portfolio, Transactions and a
 * bottom-anchored "Sell All and Quit". Controllers wire actions to the exposed buttons.
 */
public class SideBar extends VBox {

  private final Label playerName = new Label();
  private final Label playerRank = new Label();
  private final Label weeksTraded = new Label();
  private final Button marketButton = new Button("Market");
  private final Button portfolioButton = new Button("Portfolio");
  private final Button transactionButton = new Button("Transactions");
  private final Button saveButton = new Button("Save Game");
  private final Button sellAllButton = new Button("Sell All and Quit");

  /** Creates the sidebar layout with profile information, navigation buttons, and game actions. */
  public SideBar() {
    getStyleClass().add("side-bar");
    saveButton.getStyleClass().add("save-game-button");
    sellAllButton.getStyleClass().add("sell-all-button");

    for (Button b :
        new Button[] {
          marketButton, portfolioButton, transactionButton, saveButton, sellAllButton
        }) {
      b.setMaxWidth(Double.MAX_VALUE);
    }

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    getChildren()
        .addAll(
            buildProfileCard(),
            new Label("NAVIGATION"),
            marketButton,
            portfolioButton,
            transactionButton,
            spacer,
            saveButton,
            sellAllButton);
  }

  /**
   * Refreshes the compact profile card from the active player.
   *
   * @param player the active player
   */
  public void refreshProfile(Player player) {
    playerName.setText(player.getName());
    playerRank.setText(player.getStatus().toString());
    weeksTraded.setText(String.valueOf(player.getTransactionArchive().countDistinctWeeks()));
  }

  /**
   * Returns the market navigation button.
   *
   * @return the market button
   */
  public Button getMarketButton() {
    return marketButton;
  }

  /**
   * Returns the portfolio navigation button.
   *
   * @return the portfolio button
   */
  public Button getPortfolioButton() {
    return portfolioButton;
  }

  /**
   * Returns the transactions navigation button.
   *
   * @return the transactions button
   */
  public Button getTransactionButton() {
    return transactionButton;
  }

  /**
   * Returns the save-game action button.
   *
   * @return the save button
   */
  public Button getSaveButton() {
    return saveButton;
  }

  /**
   * Returns the sell-all-and-quit action button.
   *
   * @return the sell all button
   */
  public Button getSellAllButton() {
    return sellAllButton;
  }

  private VBox buildProfileCard() {
    Label nameTitle = new Label("PLAYER");
    nameTitle.getStyleClass().add("profile-title");
    playerName.getStyleClass().add("profile-name");

    VBox rankBox = profileMetric("RANK", playerRank);
    VBox weeksBox = profileMetric("WEEKS TRADED", weeksTraded);

    VBox card = new VBox(nameTitle, playerName, rankBox, weeksBox);
    card.getStyleClass().add("profile-card");
    return card;
  }

  private VBox profileMetric(String title, Label value) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("profile-metric-title");
    value.getStyleClass().add("profile-metric-value");
    VBox box = new VBox(titleLabel, value);
    box.getStyleClass().add("profile-metric");
    return box;
  }
}
