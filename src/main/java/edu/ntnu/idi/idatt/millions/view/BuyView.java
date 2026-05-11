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
 * View for the buy order popup.
 *
 * <p>The view displays the selected stock, quantity input, estimated total cost,
 * and action buttons. Buying logic and validation are handled by the controller.</p>
 */
public class BuyView {
  private final VBox root;

  private final Label stockSymbolValue;
  private final Label stockPriceValue;
  private final TextField quantityField;
  private final Label totalCostValue;
  private final Label availableCashValue;
  private final Label errorLabel;

  private final Button cancelButton;
  private final Button confirmButton;

  /**
   * Creates the buy order popup layout.
   */
  public BuyView() {
    Label titleLabel = new Label("BUY ORDER");
    stockSymbolValue = new Label();
    stockPriceValue = new Label();
    quantityField = new TextField();
    quantityField.setPromptText("Enter quantity");
    totalCostValue = new Label("0.00");
    availableCashValue = new Label();

    errorLabel = new Label();
    errorLabel.setWrapText(true);

    cancelButton = new Button("CANCEL");
    confirmButton = new Button("CONFIRM BUY");
    confirmButton.setDefaultButton(true);

    VBox infoBox = new VBox(
            12,
            row("Stock:", stockSymbolValue),
            row("Price per Share ($):", stockPriceValue),
            row("Quantity:", quantityField)
    );

    VBox summaryBox = new VBox(
            12,
            row("Total Cost ($):", totalCostValue),
            row("Available Cash ($):", availableCashValue)
    );

    HBox buttonRow = new HBox(
            12,
            cancelButton,
            confirmButton
    );
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
    root.setPrefWidth(420);
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
   * Returns the root node used as the content of the buy popup.
   *
   * @return root node for this view
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Returns the text field where the player enters the quantity to buy.
   *
   * @return quantity input field
   */
  public TextField getQuantityField() {
    return quantityField;
  }

  /**
   * Returns the button that cancels the buy order.
   *
   * @return cancel button
   */
  public Button getCancelButton() {
    return cancelButton;
  }

  /**
   * Returns the button that confirms the buy order.
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
   * Sets the price per share displayed in the popup.
   *
   * @param price price per share to display
   */
  public void setStockPrice(String price) {
    stockPriceValue.setText(price);
  }

  /**
   * Sets the calculated total cost displayed in the popup.
   *
   * @param totalCost total cost to display
   */
  public void setTotalCost(String totalCost) {
    totalCostValue.setText(totalCost);
  }

  /**
   * Sets the player's available cash displayed in the popup.
   *
   * @param cash available cash to display
   */
  public void setAvailableCash(String cash) {
    availableCashValue.setText(cash);
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
