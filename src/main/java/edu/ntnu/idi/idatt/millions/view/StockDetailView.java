package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import edu.ntnu.idi.idatt.millions.view.components.ViewHelpers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Detail view for a single stock. Shows a vertical info list and BUY action
 * on the left, with the price history chart and weekly breakdown on the right.
 * Refreshes when the session updates.
 */
public class StockDetailView extends BorderPane implements Observer {

  private Stock currentStock;

  private final Label kickerLabel = new Label("STOCK DETAIL");
  private final Label viewingLabel = new Label();
  private final Button backButton = new Button("← BACK TO MARKET");
  private final Button buyButton = new Button("BUY");

  private final Label symbolValue = new Label();
  private final Label companyValue = new Label();
  private final Label priceValue = new Label();
  private final Label changeValue = new Label();
  private final Label highValue = new Label();
  private final Label lowValue = new Label();

  private final CategoryAxis weekAxis = new CategoryAxis();
  private final NumberAxis priceAxis = new NumberAxis();
  private final AreaChart<String, Number> historyChart =
      new AreaChart<>(weekAxis, priceAxis);
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
    setupHistoryChart();
    setupHistoryColumns();
    buildLayout();
    session.addObserver(this);
  }

  private void buildLayout() {
    setPadding(new Insets(10));
    backButton.getStyleClass().add("text-link");
    buyButton.getStyleClass().add("buy-button-large");
    buyButton.setMaxWidth(Double.MAX_VALUE);

    VBox content = new VBox(buildHeader(), buildBody());
    content.setSpacing(14);
    setCenter(ViewHelpers.pageScrollPane(content));
  }

  private Node buildHeader() {
    kickerLabel.getStyleClass().add("detail-kicker");
    viewingLabel.getStyleClass().add("detail-viewing");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox row = new HBox(12, kickerLabel, viewingLabel, spacer, backButton);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("detail-header");
    return row;
  }

  private Node buildBody() {
    VBox infoPanel = buildInfoPanel();
    infoPanel.setPrefWidth(360);
    infoPanel.setMinWidth(320);

    Region separator = new Region();
    separator.getStyleClass().add("side-separator");
    separator.setMaxHeight(Double.MAX_VALUE);

    VBox rightPanel = buildRightPanel();
    HBox.setHgrow(rightPanel, Priority.ALWAYS);

    HBox body = new HBox(infoPanel, separator, rightPanel);
    body.setSpacing(20);
    return body;
  }

  private VBox buildInfoPanel() {
    Label sectionHeader = new Label("STOCK INFORMATION");
    sectionHeader.getStyleClass().add("detail-section-header");

    VBox rows = new VBox(
        infoRow("SYMBOL", symbolValue),
        infoRow("COMPANY", companyValue),
        infoRow("CURRENT PRICE", priceValue),
        infoRow("PRICE CHANGE", changeValue),
        infoRow("52-WEEK HIGH", highValue),
        infoRow("52-WEEK LOW", lowValue)
    );

    VBox panel = new VBox(sectionHeader, rows, buyButton);
    panel.setSpacing(14);
    return panel;
  }

  private Node infoRow(String label, Label valueLabel) {
    Label labelNode = new Label(label);
    labelNode.getStyleClass().add("info-row-label");
    valueLabel.getStyleClass().add("info-row-value");
    VBox row = new VBox(labelNode, valueLabel);
    row.getStyleClass().add("info-row");
    return row;
  }

  private VBox buildRightPanel() {
    Label chartHeader = new Label("PRICE HISTORY");
    chartHeader.getStyleClass().add("portfolio-panel-header");
    VBox chartPanel = new VBox(chartHeader, historyChart);
    chartPanel.getStyleClass().add("portfolio-panel");

    Label tableHeader = new Label("WEEKLY BREAKDOWN");
    tableHeader.getStyleClass().add("detail-section-header");
    ViewHelpers.autoSizeTable(historyTable);

    VBox panel = new VBox(chartPanel, tableHeader, ViewHelpers.tableWrapper(historyTable));
    panel.setSpacing(14);
    return panel;
  }

  private void setupHistoryChart() {
    priceAxis.setForceZeroInRange(false);
    priceAxis.setMinorTickVisible(false);
    historyChart.setAnimated(false);
    historyChart.setCreateSymbols(false);
    historyChart.setLegendVisible(false);
    historyChart.setHorizontalGridLinesVisible(false);
    historyChart.setVerticalGridLinesVisible(false);
    historyChart.setPrefHeight(360);
  }

  private void setupHistoryColumns() {
    TableColumn<PriceHistoryEntry, String> weekCol = new TableColumn<>("WEEK");
    weekCol.setCellValueFactory(c ->
        new SimpleStringProperty("W" + c.getValue().week()));
    weekCol.setCellFactory(col -> {
      TableCell<PriceHistoryEntry, String> cell = new TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty || item == null ? "" : item);
        }
      };
      cell.getStyleClass().add("week-cell");
      return cell;
    });

    TableColumn<PriceHistoryEntry, String> priceCol = new TableColumn<>("PRICE ($)");
    priceCol.setCellValueFactory(c ->
        new SimpleStringProperty(Money.format(c.getValue().price()).substring(1)));

    TableColumn<PriceHistoryEntry, String> movementCol = new TableColumn<>("MOVEMENT ($)");
    movementCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().movement() == null
            ? "—" : Money.formatWithArrow(c.getValue().movement())));
    movementCol.setCellFactory(col -> coloredCell(PriceHistoryEntry::movement));

    TableColumn<PriceHistoryEntry, String> percentMovementCol = new TableColumn<>("MOVEMENT (%)");
    percentMovementCol.setCellValueFactory(c -> {
      BigDecimal pct = c.getValue().percentMovement();
      return new SimpleStringProperty(pct == null ? "—" : Percentages.formatWithArrow(pct));
    });
    percentMovementCol.setCellFactory(col -> coloredCell(PriceHistoryEntry::percentMovement));

    historyTable.getColumns().addAll(weekCol, priceCol, movementCol, percentMovementCol);
    historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
  }

  private static TableCell<PriceHistoryEntry, String> coloredCell(
      java.util.function.Function<PriceHistoryEntry, BigDecimal> valueExtractor) {
    return new TableCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll("gain", "loss");
        setText(empty || item == null ? "" : item);
        if (!empty && getTableRow() != null && getTableRow().getItem() != null) {
          BigDecimal value = valueExtractor.apply(getTableRow().getItem());
          if (value != null) {
            if (value.signum() > 0) {
              getStyleClass().add("gain");
            } else if (value.signum() < 0) {
              getStyleClass().add("loss");
            }
          }
        }
      }
    };
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

    viewingLabel.setText("Viewing " + currentStock.getSymbol() + " — " + currentStock.getCompany());
    symbolValue.setText(currentStock.getSymbol());
    companyValue.setText(currentStock.getCompany());
    priceValue.setText(Money.format(currentStock.getSalesPrice()));
    changeValue.setText(Money.formatWithArrow(currentStock.getLatestPriceChange()));
    highValue.setText(Money.format(currentStock.getHighestPrice()));
    lowValue.setText(Money.format(currentStock.getLowestPrice()));
    buyButton.setText("BUY " + currentStock.getSymbol());

    setColorClass(changeValue, currentStock.getLatestPriceChange());

    List<BigDecimal> prices = currentStock.getHistoricalPrices();
    updateHistoryChart(prices);
    historyTable.getItems().setAll(buildHistoryEntries(prices));
  }

  private static void setColorClass(Label label, BigDecimal value) {
    label.getStyleClass().removeAll("gain", "loss");
    if (value.signum() > 0) {
      label.getStyleClass().add("gain");
    } else if (value.signum() < 0) {
      label.getStyleClass().add("loss");
    }
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
      BigDecimal percentMovement = (i == 0) ? null : Percentages.change(prices.get(i - 1), price);
      entries.add(new PriceHistoryEntry(i + 1, price, movement, percentMovement));
    }
    return entries;
  }

  /**
   * Row entry for the price history table.
   *
   * @param week     the week number
   * @param price    the closing price for that week
   * @param movement change vs. previous week, or null for week 1
   * @param percentMovement percentage change vs. previous week, or null for week 1
   */
  private record PriceHistoryEntry(int week, BigDecimal price, BigDecimal movement,
                                   BigDecimal percentMovement) {}

  public Button getBackButton() {
    return backButton;
  }

  public Button getBuyButton() {
    return buyButton;
  }
}
