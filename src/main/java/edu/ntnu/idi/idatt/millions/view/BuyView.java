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

  private final Label stockSymbol;
  private final Label stockPrice;
  private final TextField quantityField;
  private final Label totalCostPrice;
  private final Label availableCash;
  private final Label errorLabel;

  private final Button cancelButton;
  private final Button confirmButton;

  public BuyView() {
    Label titleLabel = new Label("BUY ORDER");

    Label stockSymbolLabel = new Label("Stock:");
    stockSymbol = new Label();

    Label stockPriceLabel = new Label("Price per Share ($):");
    stockPrice = new Label();

    Label quantityLabel = new Label("Quantity:");
    quantityField = new TextField();
    quantityField.setPromptText("Enter quantity");

    Label totalCostLabel = new Label("Total Cost ($):");
    totalCostPrice = new Label("0.00");

    Label availableCashLabel = new Label("Available Cash: ");
    availableCash = new Label();

    errorLabel = new Label();
    errorLabel.setWrapText(true);

    cancelButton = new Button("CANCEL");
    confirmButton = new Button("CONFIRM BUY");
    confirmButton.setDefaultButton(true);

    VBox infoBox = new VBox(
            12,
            row(stockSymbolLabel, stockSymbol),
            row(stockPriceLabel, stockPrice),
            row(quantityLabel, quantityField)
    );

    VBox summaryBox = new VBox(
            12,
            row(totalCostLabel, totalCostPrice),
            row(availableCashLabel, availableCash)
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

  private HBox row(Label label, Node value) {
    HBox row = new HBox(12, label, value);
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
    stockSymbol.setText(symbol);
  }

  public void setPricePerShare(String price) {
    stockPrice.setText(price);
  }

  public void setTotalCost(String totalCost) {
    totalCostPrice.setText(totalCost);
  }

  public void setAvailableCash(String cash) {
    availableCash.setText(cash);
  }

  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  public void clearErrorMessage() {
    errorLabel.setText("");
  }
}
