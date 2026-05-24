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
 * <p>The view displays the selected holding, estimated sale result, and action
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
  private final Label grossValue;
  private final Label commissionValue;
  private final Label taxValue;
  private final Label gainLossValue;
  private final Label cashReceivedValue;
  private final Label errorLabel;

  private final Button cancelButton;
  private final Button confirmButton;

  /**
   * Creates the sell order popup layout.
   */
  public SellView() {
    Label titleLabel = new Label("SELL ORDER");
    titleLabel.getStyleClass().add("popup-title");

    stockSymbolValue = new Label();
    companyNameValue = new Label();

    quantityValue = new Label();
    quantityInput = new TextField();
    quantityInput.setPromptText("Quantity to sell");

    purchasePriceValue = new Label();
    currentPriceValue = new Label();
    grossValue = new Label();
    commissionValue = new Label();
    taxValue = new Label();
    gainLossValue = new Label();
    gainLossValue.getStyleClass().add("popup-gain-value");
    cashReceivedValue = new Label();
    cashReceivedValue.getStyleClass().add("popup-total-value");

    errorLabel = new Label();
    errorLabel.getStyleClass().add("popup-error");
    errorLabel.setWrapText(true);

    cancelButton = new Button("CANCEL");
    cancelButton.getStyleClass().add("popup-secondary-button");
    confirmButton = new Button("CONFIRM SELL");
    confirmButton.getStyleClass().add("popup-primary-button");
    confirmButton.setDefaultButton(true);

    Label detailsHeader = new Label("DETAILS");
    detailsHeader.getStyleClass().add("popup-section-header");

    VBox infoBox = new VBox(
            10,
            detailsHeader,
            row("Stock:", stockSymbolValue),
            row("Company:", companyNameValue),
            row("Quantity owned:", quantityValue),
            row("Quantity to sell:", quantityInput),
            row("Average Purchase Price per Share ($):", purchasePriceValue),
            row("Current Price per Share ($):", currentPriceValue)
    );

    Label summaryHeader = new Label("SUMMARY");
    summaryHeader.getStyleClass().add("popup-section-header");

    VBox summaryBox = new VBox(
            10,
            summaryHeader,
            row("Gross ($):", grossValue),
            row("Commission ($):", commissionValue),
            row("Tax ($):", taxValue),
            row("Cash Received ($):", cashReceivedValue),
            row("Gain / Loss ($):", gainLossValue)
    );

    HBox buttonRow = new HBox(12, cancelButton, confirmButton);
    buttonRow.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(
            16,
            titleLabel,
            infoBox,
            new Separator(),
            summaryBox,
            errorLabel,
            buttonRow
    );
    root.getStyleClass().add("transaction-popup");
    root.setSpacing(16);
    root.setPadding(new Insets(24));
    root.setPrefWidth(480);
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
   * Sets the owned quantity displayed in the popup.
   *
   * @param quantity owned quantity to display
   */
  public void setQuantity(String quantity) {
    quantityValue.setText(quantity);
  }

  /**
   * Returns the text field where the user enters the quantity to sell.
   *
   * @return the quantity input field
   */
  public TextField getQuantityField() {
    return quantityInput;
  }

  /**
   * Returns the quantity currently entered for the sell order.
   *
   * @return the entered quantity text
   */
  public String getQuantityToSell() {
    return quantityInput.getText();
  }

  /**
   * Sets the quantity currently entered for the sell order.
   *
   * @param quantity quantity text to set
   */
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
   * Sets the estimated gain or loss displayed in the popup, with a green
   * or red color class applied based on sign for consistency with the
   * portfolio gain/loss column.
   *
   * @param gainLoss formatted gain or loss text
   * @param signum the sign of the realized return: 1 for gain, -1 for loss, 0 for neutral
   */
  public void setGainLoss(String gainLoss, int signum) {
    gainLossValue.setText(gainLoss);
    gainLossValue.getStyleClass().removeAll("gain", "loss");
    if (signum > 0) {
      gainLossValue.getStyleClass().add("gain");
    } else if (signum < 0) {
      gainLossValue.getStyleClass().add("loss");
    }
  }

  /**
   * Sets the estimated cash the player will receive (gross minus commission
   * and tax) displayed in the popup.
   *
   * @param cashReceived cash amount to display
   */
  public void setCashReceived(String cashReceived) {
    cashReceivedValue.setText(cashReceived);
  }

  /**
   * Sets the gross amount of the sale (current price × quantity).
   *
   * @param gross gross amount to display
   */
  public void setGross(String gross) {
    grossValue.setText(gross);
  }

  /**
   * Sets the commission portion of the sale.
   *
   * @param commission commission amount to display
   */
  public void setCommission(String commission) {
    commissionValue.setText(commission);
  }

  /**
   * Sets the tax portion of the sale (calculated on profit).
   *
   * @param tax tax amount to display
   */
  public void setTax(String tax) {
    taxValue.setText(tax);
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
