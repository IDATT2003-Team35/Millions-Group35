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

public class TransactionReceiptView {
  private final VBox root;

  private final Label titleLabel;
  private final Label stockSymbolValue;
  private final Label companyNameValue;
  private final Label quantityValue;
  private final Label priceValue;
  private final Label grossValue;
  private final Label commissionValue;
  private final Label taxValue;
  private final HBox taxRow;
  private final Label totalLabel;
  private final Label totalValue;
  private final Label weekValue;
  private final Button closeButton;

  public TransactionReceiptView() {
    titleLabel = new Label("TRANSACTION RECEIPT");

    stockSymbolValue = new Label();
    companyNameValue = new Label();
    quantityValue = new Label();
    priceValue = new Label();

    grossValue = new Label();
    commissionValue = new Label();
    taxValue = new Label();
    taxRow = row("Tax ($):", taxValue);
    totalLabel = new Label("Total:");
    totalValue = new Label();

    weekValue = new Label();

    closeButton = new Button("CLOSE");
    closeButton.setDefaultButton(true);

    VBox detailsBox = new VBox(
        10,
        row("Stock:", stockSymbolValue),
        row("Company:", companyNameValue),
        row("Quantity:", quantityValue),
        row("Price per Share ($):", priceValue),
        new Separator(),
        row("Gross ($):", grossValue),
        row("Commission ($):", commissionValue),
        taxRow,
        row(totalLabel, totalValue),
        new Separator(),
        row("Week:", weekValue)
    );

    HBox buttonRow = new HBox(closeButton);
    buttonRow.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, titleLabel, detailsBox, buttonRow);
    root.setPadding(new Insets(20));
    root.setPrefWidth(430);
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

  public Button getCloseButton() {
    return closeButton;
  }

  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  public void setStockSymbol(String stock) {
    stockSymbolValue.setText(stock);
  }

  public void setCompanyName(String company) {
    companyNameValue.setText(company);
  }

  public void setQuantity(String quantity) {
    quantityValue.setText(quantity);
  }

  public void setPrice(String price) {
    priceValue.setText(price);
  }

  public void setGross(String gross) {
    grossValue.setText(gross);
  }

  public void setCommission(String commission) {
    commissionValue.setText(commission);
  }

  public void setTax(String tax) {
    taxValue.setText(tax);
  }

  public void setTaxVisible(boolean visible) {
    taxRow.setVisible(visible);
    taxRow.setManaged(visible);
  }

  public void setTotalLabel(String label) {
    totalLabel.setText(label);
  }

  public void setTotal(String total) {
    totalValue.setText(total);
  }

  public void setWeek(String week) {
    weekValue.setText(week);
  }
}
