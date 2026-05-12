package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Detail view for a single stock. Shows price info and history,
 * and refreshes itself when the game session changes.
 */
public class StockDetailView extends BorderPane implements Observer {

  private final GameSession session;
  private Stock currentStock;

  private final Label titleLabel = new Label("STOCK DETAIL");
  private final Button backButton = new Button("Back to Market");

  private final Label symbolValue = new Label();
  private final Label companyValue = new Label();
  private final Label priceValue = new Label();
  private final Label highValue = new Label();
  private final Label lowValue = new Label();
  private final Label changeValue = new Label();
  private final Button buyButton = new Button("BUY");

  private final CategoryAxis weekAxis = new CategoryAxis();
  private final NumberAxis priceAxis = new NumberAxis();
  private final LineChart<String, Number> historyChart =
      new LineChart<>(weekAxis, priceAxis);
  private final TableView<PriceHistoryEntry> historyTable = new TableView<>();

  /**
   * Creates a new stock detail view bound to the given game session.
   *
   * @param session the active game session; must not be null
   * @throws IllegalArgumentException if session is null
   */
  public StockDetailView(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    this.session = session;
    setupHistoryChart();
    setupHistoryColumns();
    buildLayout();
    session.addObserver(this);
  }

  private void buildLayout() {
    setPadding(new Insets(10));
    setTop(buildHeader());
    setLeft(buildInfoPanel());
    setCenter(buildHistoryPanel());
  }

  /**
   * Sets the stock to display and refreshes the view.
   *
   * @param stock the stock to show; must not be null
   * @throws IllegalArgumentException if stock is null
   */
  public void displayStock(Stock stock) {
    if (stock == null) {
      throw new IllegalArgumentException("Stock cannot be null");
    }
    this.currentStock = stock;
    refresh();
  }

  private Node buildHeader(){
    BorderPane titleRow = new BorderPane();
    titleRow.setLeft(titleLabel);
    titleRow.setRight(backButton);

    Separator separator = new Separator();

    VBox header = new VBox(titleRow, separator);
    header.setSpacing(8);
    header.setPadding(new Insets(0, 0, 10, 0));
    return header;
  }

  private Node buildInfoPanel() {
    Label sectionTitle = new Label("STOCK INFORMATION");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(8);
    addRow(grid, 0, "Symbol:", symbolValue);
    addRow(grid, 1, "Company:", companyValue);
    addRow(grid, 2, "Current Price ($):", priceValue);
    addRow(grid, 3, "52-Week High ($):", highValue);
    addRow(grid, 4, "52-Week Low ($):", lowValue);
    addRow(grid, 5, "Price Change ($):", changeValue);

    VBox panel = new VBox(sectionTitle, grid, buyButton);
    panel.setSpacing(12);
    panel.setPadding(new Insets(10, 20, 10, 0));
    panel.setPrefWidth(300);
    return panel;
  }

  private void addRow(GridPane grid, int row, String labelText, Label valueLabel) {
    grid.add(new Label(labelText), 0, row);
    grid.add(valueLabel, 1, row);
  }

  private Node buildHistoryPanel() {
    Label sectionTitle = new Label("PRICE HISTORY");
    Label chartTitle = new Label("WEEKLY CLOSING PRICE");
    Label tableTitle = new Label("WEEKLY BREAKDOWN");

    VBox panel = new VBox(sectionTitle, chartTitle, historyChart, tableTitle, historyTable);
    panel.setSpacing(8);
    panel.setPadding(new Insets(10, 0, 10, 10));
    VBox.setVgrow(historyTable, Priority.ALWAYS);
    return panel;
  }

  private void setupHistoryChart() {
    priceAxis.setForceZeroInRange(false);
    historyChart.setAnimated(false);
    historyChart.setCreateSymbols(true);
    historyChart.setLegendVisible(false);
    historyChart.setPrefHeight(320);
  }

  private void setupHistoryColumns() {
    TableColumn<PriceHistoryEntry, String> weekCol = new TableColumn<>("Week");
    weekCol.setCellValueFactory(c ->
        new SimpleStringProperty("Week " + c.getValue().week()));

    TableColumn<PriceHistoryEntry, String> priceCol = new TableColumn<>("Price ($)");
    priceCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().price().toPlainString()));

    TableColumn<PriceHistoryEntry, String> movementCol = new TableColumn<>("Movement");
    movementCol.setCellValueFactory(c ->
        new SimpleStringProperty(formatMovement(c.getValue().movement())));

    historyTable.getColumns().addAll(weekCol, priceCol, movementCol);
  }

  private static String formatMovement(BigDecimal movement) {
    if (movement == null) {
      return "--";
    }
    String sign = movement.signum() >= 0 ? "+" : "";
    return sign + movement.toPlainString();
  }

  /**
   * Row entry for the price history table.
   *
   * @param week     the week number
   * @param price    the closing price for that week
   * @param movement change vs. previous week, or null for week 1
   */
  private record PriceHistoryEntry(int week, BigDecimal price, BigDecimal movement) {}
  @Override
  public void update() {
    if (currentStock != null) {
      refresh();
    }
  }

  private void refresh() {
    if (currentStock == null) {
      return;
    }

    symbolValue.setText(currentStock.getSymbol());
    companyValue.setText(currentStock.getCompany());
    priceValue.setText(currentStock.getSalesPrice().toPlainString());
    highValue.setText(currentStock.getHighestPrice().toPlainString());
    lowValue.setText(currentStock.getLowestPrice().toPlainString());
    changeValue.setText(formatMovement(currentStock.getLatestPriceChange()));

    List<BigDecimal> prices = currentStock.getHistoricalPrices();
    updateHistoryChart(prices);
    historyTable.getItems().setAll(buildHistoryEntries(prices));
  }

  private void updateHistoryChart(List<BigDecimal> prices) {
    XYChart.Series<String, Number> series = new XYChart.Series<>();

    for (int i = 0; i < prices.size(); i++) {
      series.getData().add(new XYChart.Data<>("W" + (i + 1), prices.get(i)));
    }

    historyChart.getData().setAll(series);
  }

  private static List<PriceHistoryEntry> buildHistoryEntries(List<BigDecimal> prices) {
    List<PriceHistoryEntry> entries = new ArrayList<>();
    for (int i = 0; i < prices.size(); i++) {
      BigDecimal price = prices.get(i);
      BigDecimal movement = (i == 0) ? null : price.subtract(prices.get(i - 1));
      entries.add(new PriceHistoryEntry(i + 1, price, movement));
    }
    return entries;
  }

  public Button getBackButton() {
    return backButton;
  }

  public Button getBuyButton() {
    return buyButton;
  }
}
