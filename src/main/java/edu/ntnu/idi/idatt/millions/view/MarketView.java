package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import edu.ntnu.idi.idatt.millions.util.TableColumns;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

/**
 * Market view showing all listed stocks with search, plus a side panel
 * with top gainers and losers. Refreshes itself when the game session changes.
 */
public class MarketView extends BorderPane implements Observer {

  private final GameSession session;

  private final Label titleLabel = new Label("MARKET");
  private final TextField searchField = new TextField();
  private final Label countLabel = new Label();

  private final TableView<Stock> stockTable = new TableView<>();

  private final VBox gainersBox = new VBox();
  private final VBox losersBox = new VBox();

  private Runnable onUpdate = () -> { };

  /**
   * Creates a new market view bound to the given game session.
   *
   * @param session the active game session; must not be null
   * @throws IllegalArgumentException if session is null
   */
  public MarketView(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session can not be null");
    }
    this.session = session;

    setupColumns();
    buildLayout();
    session.addObserver(this);
  }

  private void buildLayout() {
    setPadding(new Insets(10));
    setTop(buildHeader());
    setCenter(stockTable);
    setRight(buildSidePanel());
  }

  private Node buildHeader() {
    VBox header = new VBox(titleLabel, searchField, countLabel);
    header.setSpacing(8);
    header.setPadding(new Insets(0, 0, 10, 0));
    return header;
  }

  private Node buildSidePanel() {
    VBox panel = new VBox(
        new Label("Top Gainers"), gainersBox,
        new Label("Top Losers"), losersBox
    );
    panel.setSpacing(8);
    panel.setPadding(new Insets(0, 0, 0, 10));
    return panel;
  }

  private void setupColumns() {
    TableColumn<Stock, String> symbolCol = new TableColumn<>("Symbol");
    symbolCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getSymbol()));

    TableColumn<Stock, String> companyCol = new TableColumn<>("Company");
    companyCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getCompany()));

    TableColumn<Stock, BigDecimal> priceCol = new TableColumn<>("Price ($)");
    priceCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSalesPrice()));

    TableColumn<Stock, BigDecimal> changeCol = TableColumns.numericColumn(
        "Change ($)", Stock::getLatestPriceChange, MarketView::formatChange);
    TableColumn<Stock, BigDecimal> percentChangeCol = TableColumns.numericColumn(
        "Change (%)", Stock::getLatestPercentChange, Percentages::format);
    TableColumn<Stock, BigDecimal> allTimeChangeCol = TableColumns.numericColumn(
        "All-Time Change (%)", Stock::getTotalPercentChange, Percentages::format);

    stockTable.getColumns().addAll(
        symbolCol, companyCol, priceCol, changeCol, percentChangeCol, allTimeChangeCol);
  }

  @Override
  public void update() {
    onUpdate.run();
  }

  /**
   * Registers a callback invoked when the view receives an Observer update.
   * The controller uses this to re-apply its current filter and push fresh data.
   *
   * @param callback the runnable to execute on each update; must not be null
   * @throws IllegalArgumentException if callback is null
   */
  public void setOnUpdate(Runnable callback) {
    if (callback == null) {
      throw new IllegalArgumentException("Callback cannot be null");
    }
    this.onUpdate = callback;
  }

  /**
   * Replaces the stocks currently shown in the main table and re-applies any
   * active column sort so the user's chosen order persists across updates.
   *
   * @param stocks the stocks to display
   */
  public void setStocks(List<Stock> stocks) {
    stockTable.getItems().setAll(stocks);
    stockTable.sort();
  }

  /**
   * Updates the count label text shown above the table.
   *
   * @param text the text to display
   */
  public void setCount(String text) {
    countLabel.setText(text);
  }

  /**
   * Replaces the entries shown in the Top Gainers side panel.
   *
   * @param stocks the gaining stocks to list
   */
  public void setGainers(List<Stock> stocks) {
    populateTopList(gainersBox, stocks);
  }

  /**
   * Replaces the entries shown in the Top Losers side panel.
   *
   * @param stocks the losing stocks to list
   */
  public void setLosers(List<Stock> stocks) {
    populateTopList(losersBox, stocks);
  }

  private void populateTopList(VBox box, List<Stock> stocks) {
    box.getChildren().clear();
    for (Stock stock : stocks) {
      String text = stock.getSymbol() + "  " + Percentages.format(stock.getLatestPercentChange());
      box.getChildren().add(new Label(text));
    }
  }

  private static String formatChange(BigDecimal change) {
    String sign = change.signum() >= 0 ? "+" : "";
    return sign + change.toPlainString();
  }

  public TextField getSearchField() {
    return searchField;
  }

  public TableView<Stock> getStockTable() {
    return stockTable;
  }

  public Label getCountLabel() {
    return countLabel;
  }
}
