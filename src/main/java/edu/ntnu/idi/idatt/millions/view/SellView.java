package edu.ntnu.idi.idatt.millions.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SellView {
  private final VBox root;

  private final Label stockSymbolValue;
  private final Label companyNameValue;
  private final Label quantityValue;
  private final Label purchasePriceValue;
  private final Label currentPriceValue;
  private final Label gainLossValue;
  private final Label estimatedRevenueValue;
  private final Label errorLabel;

  private final Button cancelButton;
  private final Button confirmButton;

  public SellView() {
    Label titleLabel = new Label("SELL ORDER");

    stockSymbolValue = new Label();
    companyNameValue = new Label();
    quantityValue = new Label();
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
            row("Quantity:", quantityValue),
            row("Purchase Price per Share ($):", purchasePriceValue),
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

  public Parent getRoot() {
    return root;
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

  public void setCompanyName(String companyName) {
    companyNameValue.setText(companyName);
  }

  public void setQuantity(String quantity) {
    quantityValue.setText(quantity);
  }

  public void setPurchasePrice(String purchasePrice) {
    purchasePriceValue.setText(purchasePrice);
  }

  public void setCurrentPrice(String currentPrice) {
    currentPriceValue.setText(currentPrice);
  }

  public void setGainLoss(String gainLoss) {
    gainLossValue.setText(gainLoss);
  }

  public void setEstimatedRevenue(String estimatedRevenue) {
    estimatedRevenueValue.setText(estimatedRevenue);
  }

  public void setErrorMessage(String message) {
    errorLabel.setText(message);
  }

  public void clearErrorMessage() {
    errorLabel.setText("");
  }
}
