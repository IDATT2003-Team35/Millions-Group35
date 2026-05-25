package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.PortfolioHolding;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.TableColumns;
import edu.ntnu.idi.idatt.millions.view.components.ViewHelpers;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;

/**
 * Portfolio view showing the player's net worth graph, summary totals, and table of owned holdings.
 * Refreshes itself when the game session changes.
 */
public class PortfolioView extends BorderPane implements Observer {

  private final GameSession session;

  private final Label titleLabel = new Label("PORTFOLIO");

  private final CategoryAxis weekAxis = new CategoryAxis();
  private final NumberAxis netWorthAxis = new NumberAxis();
  private final AreaChart<String, Number> netWorthChart = new AreaChart<>(weekAxis, netWorthAxis);

  private final Label holdingsValue = new Label();
  private final Label stockValueValue = new Label();
  private final Label totalGainLossValue = new Label();
  private final Label totalGainLossPercentValue = new Label();

  private final TableView<PortfolioHolding> holdingsTable = new TableView<>();

  /** Handler invoked when a row's Sell button is clicked. No-op by default. */
  private Consumer<PortfolioHolding> sellHandler = holding -> {};

  private Runnable onUpdate = () -> {};

  /**
   * Creates a new portfolio view bound to the given game session.
   *
   * @param session the active game session; must not be null
   * @throws IllegalArgumentException if session is null
   */
  public PortfolioView(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    this.session = session;

    setupNetWorthChart();
    setupColumns();
    buildLayout();
    session.addObserver(this);
  }

  private void buildLayout() {
    setPadding(new Insets(10));
    titleLabel.getStyleClass().add("section-title");
    ViewHelpers.autoSizeTable(holdingsTable);

    Node chartPanel = buildNetWorthChart();
    VBox content =
        new VBox(
            titleLabel, chartPanel, buildSummaryBar(), ViewHelpers.tableWrapper(holdingsTable));
    content.setSpacing(14);

    setCenter(ViewHelpers.pageScrollPane(content));
  }

  private Node buildNetWorthChart() {
    Label chartTitle = new Label("NET WORTH OVER TIME");
    chartTitle.getStyleClass().add("portfolio-panel-header");

    VBox chartBox = new VBox(chartTitle, netWorthChart);
    chartBox.getStyleClass().add("portfolio-panel");
    return chartBox;
  }

  private void setupNetWorthChart() {
    netWorthAxis.setForceZeroInRange(false);
    netWorthAxis.setMinorTickVisible(false);
    netWorthChart.setAnimated(false);
    netWorthChart.setCreateSymbols(false);
    netWorthChart.setLegendVisible(false);
    netWorthChart.setHorizontalGridLinesVisible(false);
    netWorthChart.setVerticalGridLinesVisible(false);
    netWorthChart.setPrefHeight(420);
  }

  private Node buildSummaryBar() {
    HBox bar =
        new HBox(
            buildSummaryBox("HOLDINGS", holdingsValue),
            buildSummaryBox("STOCK VALUE", stockValueValue),
            buildSummaryBox("TOTAL GAIN / LOSS", totalGainLossValue),
            buildSummaryBox("RETURN", totalGainLossPercentValue));
    bar.getStyleClass().add("summary-bar");
    return bar;
  }

  private Node buildSummaryBox(String title, Label valueLabel) {
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("summary-title");
    valueLabel.getStyleClass().add("summary-value");
    VBox box = new VBox(titleLabel, valueLabel);
    box.getStyleClass().add("summary-box");
    HBox.setHgrow(box, Priority.ALWAYS);
    return box;
  }

  private void setupColumns() {
    TableColumn<PortfolioHolding, String> symbolCol = new TableColumn<>("SYMBOL");
    symbolCol.setCellValueFactory(
        c -> new SimpleStringProperty(c.getValue().getStock().getSymbol()));
    symbolCol.setCellFactory(ViewHelpers.symbolCellFactory());

    TableColumn<PortfolioHolding, String> companyCol = new TableColumn<>("COMPANY");
    companyCol.setCellValueFactory(
        c -> new SimpleStringProperty(c.getValue().getStock().getCompany()));

    TableColumn<PortfolioHolding, BigDecimal> qtyCol =
        TableColumns.numericColumn("QTY", PortfolioHolding::getQuantity, BigDecimal::toPlainString);
    TableColumn<PortfolioHolding, BigDecimal> buyPriceCol =
        TableColumns.numericColumn(
            "AVG PURCHASE ($)",
            PortfolioHolding::getAveragePurchasePrice,
            v -> Money.format(v).substring(1));
    TableColumn<PortfolioHolding, BigDecimal> currentPriceCol =
        TableColumns.numericColumn(
            "CURRENT ($)", s -> s.getStock().getSalesPrice(), v -> Money.format(v).substring(1));
    TableColumn<PortfolioHolding, BigDecimal> gainLossCol =
        TableColumns.coloredNumericColumn(
            "GAIN / LOSS ($)", PortfolioHolding::getTotalGainLoss, Money::formatWithArrow);

    TableColumn<PortfolioHolding, Void> actionCol = new TableColumn<>("");
    actionCol.setCellFactory(
        col ->
            new TableCell<PortfolioHolding, Void>() {
              private final Button sellButton = new Button("SELL");

              {
                sellButton.getStyleClass().add("sell-button");
                sellButton.setOnAction(
                    e -> {
                      PortfolioHolding holding = getTableView().getItems().get(getIndex());
                      sellHandler.accept(holding);
                    });
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : sellButton);
              }
            });

    holdingsTable
        .getColumns()
        .addAll(
            symbolCol, companyCol, qtyCol, buyPriceCol, currentPriceCol, gainLossCol, actionCol);
    holdingsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
  }

  @Override
  public void update() {
    onUpdate.run();
  }

  /**
   * Registers a callback invoked when the view receives an Observer update. The controller uses
   * this to push fresh data to the view.
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
   * Sets the value shown in the Holdings summary box.
   *
   * @param count number of distinct holdings held
   */
  public void setHoldingsCount(int count) {
    holdingsValue.setText(String.valueOf(count));
  }

  /**
   * Sets the value shown in the Stock Value summary box.
   *
   * @param text the pre-formatted dollar string to display
   */
  public void setStockValue(String text) {
    stockValueValue.setText(text);
  }

  /**
   * Sets the value shown in the Total Gain/Loss ($) summary box.
   *
   * @param text the pre-formatted signed dollar string to display
   */
  public void setTotalGainLoss(String text) {
    totalGainLossValue.setText(text);
  }

  /**
   * Sets the value shown in the Total Gain/Loss (%) summary box.
   *
   * @param text the pre-formatted percent string to display
   */
  public void setTotalGainLossPercent(String text) {
    totalGainLossPercentValue.setText(text);
  }

  /**
   * Replaces the holdings currently shown in the holdings table and re-applies any active column
   * sort so the user's chosen order persists across updates.
   *
   * @param holdings the holdings to display
   */
  public void setHoldings(List<PortfolioHolding> holdings) {
    holdingsTable.getItems().setAll(holdings);
    holdingsTable.sort();
  }

  /**
   * Replaces the data series in the net-worth chart with the given history.
   *
   * @param netWorthHistory the net worth values per week (index = week - 1)
   */
  public void setNetWorthHistory(List<BigDecimal> netWorthHistory) {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    for (int i = 0; i < netWorthHistory.size(); i++) {
      series.getData().add(new XYChart.Data<>("W" + (i + 1), netWorthHistory.get(i)));
    }
    netWorthChart.getData().setAll(series);
  }

  /**
   * Sets the handler invoked when the Sell button on a row is clicked.
   *
   * @param handler the consumer to receive the clicked holding; must not be null
   * @throws IllegalArgumentException if handler is null
   */
  public void setSellHandler(Consumer<PortfolioHolding> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    this.sellHandler = handler;
  }
}
