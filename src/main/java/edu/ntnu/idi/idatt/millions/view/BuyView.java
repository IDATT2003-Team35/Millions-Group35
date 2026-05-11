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

  public Parent getRoot() {
    return root;
  }

  public TextField getQuantityField() {
    return quantityField;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public Button getConfirmButton() {
    return confirmButton;
  }

  public void setStockSymbol(String symbol) {
    stockSymbolValue.setText(symbol);
  }

  public void setStockPrice(String price) {
    stockPriceValue.setText(price);
  }

  public void setTotalCost(String totalCost) {
    totalCostValue.setText(totalCost);
  }

  public void setAvailableCash(String cash) {
    availableCashValue.setText(cash);
  }

  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  public void clearErrorMessage() {
    errorLabel.setText("");
  }
}
