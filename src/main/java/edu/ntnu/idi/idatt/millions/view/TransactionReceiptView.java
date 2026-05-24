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

/**
 * View for displaying a completed transaction receipt.
 *
 * <p>The same receipt view is used for both purchase and sale transactions.
 * The controller decides which values and labels should be shown.</p>
 */
public class TransactionReceiptView {
  private final VBox root;

  private final Label titleLabel;
  private final Label gainAmountValue;
  private final Label gainPercentValue;
  private final VBox gainHeader;
  private final Label stockSymbolValue;
  private final Label companyNameValue;
  private final Label quantityValue;
  private final Label priceValue;
  private final Label grossValue;
  private final Label commissionValue;
  private final Label taxValue;
  private final HBox taxRow;
  private final Label costBasisValue;
  private final HBox costBasisRow;
  private final Label totalLabel;
  private final Label totalValue;
  private final Label weekValue;
  private final Button closeButton;

  /**
   * Creates the transaction receipt popup layout.
   */
  public TransactionReceiptView() {
    titleLabel = new Label("TRANSACTION RECEIPT");

    gainAmountValue = new Label();
    gainPercentValue = new Label();
    gainHeader = new VBox(4, gainAmountValue, gainPercentValue);
    gainHeader.setAlignment(Pos.CENTER);

    stockSymbolValue = new Label();
    companyNameValue = new Label();
    quantityValue = new Label();
    priceValue = new Label();

    grossValue = new Label();
    commissionValue = new Label();
    taxValue = new Label();
    taxRow = row("Tax ($):", taxValue);
    costBasisValue = new Label();
    costBasisRow = row("Cost Basis ($):", costBasisValue);
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
        costBasisRow,
        row(totalLabel, totalValue),
        new Separator(),
        row("Week:", weekValue)
    );

    HBox buttonRow = new HBox(closeButton);
    buttonRow.setAlignment(Pos.CENTER_RIGHT);

    root = new VBox(16, titleLabel, gainHeader, detailsBox, buttonRow);
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

  /**
   * Returns the root node used as the content of the receipt popup.
   *
   * @return root node for this view
   */
  public Parent getRoot() {
    return root;
  }

  /**
   * Returns the button that closes the receipt popup.
   *
   * @return close button
   */
  public Button getCloseButton() {
    return closeButton;
  }

  /**
   * Sets the receipt title.
   *
   * @param title title to display
   */
  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  /**
   * Sets the stock symbol displayed in the receipt.
   *
   * @param stock stock symbol to display
   */
  public void setStockSymbol(String stock) {
    stockSymbolValue.setText(stock);
  }

  /**
   * Sets the company name displayed in the receipt.
   *
   * @param company company name to display
   */
  public void setCompanyName(String company) {
    companyNameValue.setText(company);
  }

  /**
   * Sets the transaction quantity displayed in the receipt.
   *
   * @param quantity quantity to display
   */
  public void setQuantity(String quantity) {
    quantityValue.setText(quantity);
  }

  /**
   * Sets the price per share displayed in the receipt.
   *
   * @param price price per share to display
   */
  public void setPrice(String price) {
    priceValue.setText(price);
  }

  /**
   * Sets the gross transaction amount displayed in the receipt.
   *
   * @param gross gross amount to display
   */
  public void setGross(String gross) {
    grossValue.setText(gross);
  }

  /**
   * Sets the commission amount displayed in the receipt.
   *
   * @param commission commission amount to display
   */
  public void setCommission(String commission) {
    commissionValue.setText(commission);
  }

  /**
   * Sets the tax amount displayed in the receipt.
   *
   * @param tax tax amount to display
   */
  public void setTax(String tax) {
    taxValue.setText(tax);
  }

  /**
   * Shows or hides the tax row.
   *
   * <p>Hidden rows are also unmanaged so they do not reserve layout space.</p>
   *
   * @param visible {@code true} to show the tax row, {@code false} to hide it
   */
  public void setTaxVisible(boolean visible) {
    taxRow.setVisible(visible);
    taxRow.setManaged(visible);
  }

  /**
   * Sets the cost basis amount displayed in the receipt.
   *
   * @param costBasis cost basis (purchase price × quantity) to display
   */
  public void setCostBasis(String costBasis) {
    costBasisValue.setText(costBasis);
  }

  /**
   * Shows or hides the cost basis row.
   *
   * @param visible {@code true} to show the cost basis row, {@code false} to hide it
   */
  public void setCostBasisVisible(boolean visible) {
    costBasisRow.setVisible(visible);
    costBasisRow.setManaged(visible);
  }

  /**
   * Sets the realized gain/loss header values shown above the details.
   *
   * @param amount the realized profit or loss (e.g. "+$339.50" or "-$50.00")
   * @param percent the realized return as a percentage (e.g. "+33.95%")
   */
  public void setGainHeader(String amount, String percent) {
    gainAmountValue.setText(amount);
    gainPercentValue.setText(percent);
  }

  /**
   * Shows or hides the gain header (used only for completed sales).
   *
   * @param visible {@code true} to show the gain header, {@code false} to hide it
   */
  public void setGainHeaderVisible(boolean visible) {
    gainHeader.setVisible(visible);
    gainHeader.setManaged(visible);
  }

  /**
   * Sets the label used for the final total row.
   *
   * @param label total row label to display
   */
  public void setTotalLabel(String label) {
    totalLabel.setText(label);
  }

  /**
   * Sets the final transaction total displayed in the receipt.
   *
   * @param total total amount to display
   */
  public void setTotal(String total) {
    totalValue.setText(total);
  }

  /**
   * Sets the week number displayed in the receipt.
   *
   * @param week week number to display
   */
  public void setWeek(String week) {
    weekValue.setText(week);
  }
}
