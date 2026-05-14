package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import edu.ntnu.idi.idatt.millions.util.TableColumns;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

/**
 * Portfolio view showing the player's net worth graph, summary totals,
 * and table of owned shares. Refreshes itself when the game session changes.
 */
public class PortfolioView extends BorderPane implements Observer {

  private final GameSession session;

  private final Label titleLabel = new Label("PORTFOLIO");

  private final CategoryAxis weekAxis = new CategoryAxis();
  private final NumberAxis netWorthAxis = new NumberAxis();
  private final LineChart<String, Number> netWorthChart =
      new LineChart<>(weekAxis, netWorthAxis);

  private final Label holdingsValue = new Label();
  private final Label stockValueValue = new Label();
  private final Label totalGainLossValue = new Label();
  private final Label totalGainLossPercentValue = new Label();

  private final TableView<Share> holdingsTable = new TableView<>();

  /** Handler invoked when a row's Sell button is clicked. No-op by default. */
  private Consumer<Share> sellHandler = share -> { };
  private Runnable onUpdate = () -> { };

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
    setTop(buildHeader());
    setCenter(buildContent());
  }

  private Node buildHeader() {
    Separator separator = new Separator();
    VBox header = new VBox(titleLabel, separator);
    header.setSpacing(8);
    header.setPadding(new Insets(0, 0, 10, 0));
    return header;
  }

  private Node buildContent() {
    VBox content = new VBox(buildNetWorthChart(), buildSummaryBar(), holdingsTable);
    content.setSpacing(10);
    VBox.setVgrow(holdingsTable, Priority.ALWAYS);
    return content;
  }

  private Node buildNetWorthChart() {
    Label chartTitle = new Label("NET WORTH OVER TIME");

    VBox chartBox = new VBox(chartTitle, netWorthChart);
    chartBox.setSpacing(8);
    return chartBox;
  }

  private void setupNetWorthChart() {
    netWorthAxis.setForceZeroInRange(false);
    netWorthChart.setAnimated(false);
    netWorthChart.setCreateSymbols(true);
    netWorthChart.setLegendVisible(false);
    netWorthChart.setPrefHeight(320);
  }

  private Node buildSummaryBar() {
    HBox bar = new HBox(
        buildSummaryBox("Holdings", holdingsValue),
        buildSummaryBox("Stock Value ($)", stockValueValue),
        buildSummaryBox("Total Gain/Loss ($)", totalGainLossValue),
        buildSummaryBox("Total Gain/Loss (%)", totalGainLossPercentValue)
    );
    bar.setSpacing(10);
    return bar;
  }

  private Node buildSummaryBox(String title, Label valueLabel) {
    Label titleLabel = new Label(title);
    Separator underline = new Separator();
    VBox box = new VBox(titleLabel, underline, valueLabel);
    box.setSpacing(4);
    box.setPadding(new Insets(8));
    HBox.setHgrow(box, Priority.ALWAYS);
    return box;
  }

  private void setupColumns() {
    TableColumn<Share, String> symbolCol = new TableColumn<>("Symbol");
    symbolCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getStock().getSymbol()));

    TableColumn<Share, String> companyCol = new TableColumn<>("Company");
    companyCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getStock().getCompany()));

    TableColumn<Share, BigDecimal> qtyCol = TableColumns.numericColumn(
        "Qty", Share::getQuantity, BigDecimal::toPlainString);
    TableColumn<Share, BigDecimal> buyPriceCol = TableColumns.numericColumn(
        "Buy Price ($)", Share::getPurchasePrice, BigDecimal::toPlainString);
    TableColumn<Share, BigDecimal> currentPriceCol = TableColumns.numericColumn(
        "Current Price ($)", s -> s.getStock().getSalesPrice(), BigDecimal::toPlainString);
    TableColumn<Share, BigDecimal> gainLossCol = TableColumns.numericColumn(
        "Gain / Loss ($)", Share::getNetGainLoss, PortfolioView::formatMovement);
    TableColumn<Share, BigDecimal> gainLossPercentCol = TableColumns.numericColumn(
        "Gain / Loss (%)", Share::getNetGainLossPercent, Percentages::format);

    TableColumn<Share, Void> actionCol = new TableColumn<>("Action");
    actionCol.setCellFactory(col -> new TableCell<Share, Void>() {
      private final Button sellButton = new Button("Sell");

      {
        sellButton.setOnAction(e -> {
          Share share = getTableView().getItems().get(getIndex());
          sellHandler.accept(share);
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : sellButton);
      }
    });

    holdingsTable.getColumns().addAll(
        symbolCol, companyCol, qtyCol, buyPriceCol,
        currentPriceCol, gainLossCol, gainLossPercentCol, actionCol);
  }

  @Override
  public void update() {
    onUpdate.run();
  }

  /**
   * Registers a callback invoked when the view receives an Observer update.
   * The controller uses this to push fresh data to the view.
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
   * @param count number of distinct shares held
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
   * Replaces the shares currently shown in the holdings table and re-applies any
   * active column sort so the user's chosen order persists across updates.
   *
   * @param shares the shares to display
   */
  public void setShares(List<Share> shares) {
    holdingsTable.getItems().setAll(shares);
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
   * @param handler the consumer to receive the clicked share; must not be null
   * @throws IllegalArgumentException if handler is null
   */
  public void setSellHandler(Consumer<Share> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    this.sellHandler = handler;
  }

  private static String formatMovement(BigDecimal value) {
    String sign = value.signum() >= 0 ? "+" : "";
    return sign + value.toPlainString();
  }
}
