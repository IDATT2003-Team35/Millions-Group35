package edu.ntnu.idi.idatt.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * View for the sell order popup.
 *
 * <p>The view displays the selected share, estimated sale result, and action
 * buttons. Selling logic and validation are handled by the controller.</p>
 */
public class SellView {
  private final VBox root;

  private final Label stockSymbolValue;
  private final Label companyNameValue;
  private final Label quantityValue;
  private final TextField quantityInput;
  private final Label purchasePriceValue;
  private final Label currentPriceValue;
  private final Label gainLossValue;
  private final Label estimatedRevenueValue;
  private final Label errorLabel;

  private final Button cancelButton;
  private final Button confirmButton;

  /**
   * Creates the sell order popup layout.
   */
  public SellView() {
    Label titleLabel = new Label("SELL ORDER");

    stockSymbolValue = new Label();
    companyNameValue = new Label();

    quantityValue = new Label();
    quantityInput = new TextField();
    quantityInput.setPromptText("Quantity to sell");

    purchasePriceValue = new Label();
    currentPriceValue = new Label();
    gainLossValue = new Label();
    estimatedRevenueValue = new Label();

    errorLabel = new Label();
    errorLabel.setWrapText(true);

    cancelButton = new Button("CANCEL");
    confirmButton = new Button("CONFIRM SELL");
    confirmButton.setDefaultButton(true);

    VBox infoBox = new VBox(
            12,
            row("Stock:", stockSymbolValue),
            row("Company:", companyNameValue),
            row("Quantity owned:", quantityValue),
            row("Quantity to sell:", quantityInput),
            row("Average Purchase Price per Share ($):", purchasePriceValue),
            row("Current Price per Share ($):", currentPriceValue)
    );

    VBox summaryBox = new VBox(
            12,
            row("Gain / Loss ($):", gainLossValue),
            row("Estimated Revenue ($):", estimatedRevenueValue)
    );

    HBox buttonRow = new HBox(12, cancelButton, confirmButton);
    buttonRow.setAlignment(Pos.CENTER);

    root = new VBox(
            16,
            titleLabel,
            infoBox,
            new Separator(),
            summaryBox,
            errorLabel,
            buttonRow
    );
    root.setSpacing(16);
    root.setPadding(new Insets(20));
    root.setPrefWidth(460);
  }

  private HBox row(String labelText, Node value) {
    return row(new Label(labelText), value);
  }

  private HBox row(Label label, Node value) {
    HBox row = new HBox(12, label, value);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  /**
   * Returns the root node used as the content of the sell popup.
   *
   * @return root node for this view
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Returns the button that cancels the sell order.
   *
   * @return cancel button
   */
  public Button getCancelButton() {
    return cancelButton;
  }

  /**
   * Returns the button that confirms the sell order.
   *
   * @return confirm button
   */
  public Button getConfirmButton() {
    return confirmButton;
  }

  /**
   * Sets the stock symbol displayed in the popup.
   *
   * @param symbol stock symbol to display
   */
  public void setStockSymbol(String symbol) {
    stockSymbolValue.setText(symbol);
  }

  /**
   * Sets the company name displayed in the popup.
   *
   * @param companyName company name to display
   */
  public void setCompanyName(String companyName) {
    companyNameValue.setText(companyName);
  }

  /**
   * Sets the share quantity displayed in the popup.
   *
   * @param quantity share quantity to display
   */
  public void setQuantity(String quantity) {
    quantityValue.setText(quantity);
  }

  public String getQuantityToSell() {
    return quantityInput.getText();
  }

  public void setQuantityToSell(String quantity) {
    quantityInput.setText(quantity);
  }

  /**
   * Sets the purchase price per share displayed in the popup.
   *
   * @param purchasePrice purchase price to display
   */
  public void setPurchasePrice(String purchasePrice) {
    purchasePriceValue.setText(purchasePrice);
  }

  /**
   * Sets the current price per share displayed in the popup.
   *
   * @param currentPrice current stock price to display
   */
  public void setCurrentPrice(String currentPrice) {
    currentPriceValue.setText(currentPrice);
  }

  /**
   * Sets the estimated gain or loss displayed in the popup.
   *
   * @param gainLoss gain or loss to display
   */
  public void setGainLoss(String gainLoss) {
    gainLossValue.setText(gainLoss);
  }

  /**
   * Sets the estimated sale revenue displayed in the popup.
   *
   * @param estimatedRevenue estimated revenue to display
   */
  public void setEstimatedRevenue(String estimatedRevenue) {
    estimatedRevenueValue.setText(estimatedRevenue);
  }

  /**
   * Shows an error message in the popup.
   *
   * @param message error message to display
   */
  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  /**
   * Clears the current error message.
   */
  public void clearErrorMessage() {
    errorLabel.setText("");
  }
}
