package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Portfolio;
import edu.ntnu.idi.idatt.millions.model.Share;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Percentages;
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
  private final Label totalInvestedValue = new Label();
  private final Label currentValueValue = new Label();
  private final Label totalGainLossValue = new Label();

  private final TableView<Share> holdingsTable = new TableView<>();

  /** Handler invoked when a row's Sell button is clicked. No-op by default. */
  private Consumer<Share> sellHandler = share -> { };

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
    refresh();
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
        buildSummaryBox("Total Invested ($)", totalInvestedValue),
        buildSummaryBox("Current Value ($)", currentValueValue),
        buildSummaryBox("Total Gain/Loss ($)", totalGainLossValue)
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

    TableColumn<Share, String> qtyCol = new TableColumn<>("Qty");
    qtyCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getQuantity().toPlainString()));

    TableColumn<Share, String> purchasePriceCol = new TableColumn<>("Purchase Price ($)");
    purchasePriceCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getPurchasePrice()
            .multiply(c.getValue().getQuantity()).toPlainString()));

    TableColumn<Share, String> currentValueCol = new TableColumn<>("Current Value ($)");
    currentValueCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getStock().getSalesPrice()
            .multiply(c.getValue().getQuantity()).toPlainString()));

    TableColumn<Share, String> gainLossCol = new TableColumn<>("Gain / Loss ($)");
    gainLossCol.setCellValueFactory(c ->
        new SimpleStringProperty(formatMovement(c.getValue().getStock().getSalesPrice()
            .subtract(c.getValue().getPurchasePrice())
            .multiply(c.getValue().getQuantity()))));

    TableColumn<Share, String> gainLossPercentCol = new TableColumn<>("Gain / Loss (%)");
    gainLossPercentCol.setCellValueFactory(c ->
        new SimpleStringProperty(Percentages.format(Percentages.change(
            c.getValue().getPurchasePrice(),
            c.getValue().getStock().getSalesPrice()))));

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
        symbolCol, companyCol, qtyCol, purchasePriceCol,
        currentValueCol, gainLossCol, gainLossPercentCol, actionCol);
  }

  @Override
  public void update() {
    refresh();
  }

  /**
   * Refreshes the chart, summary bar and holdings table from the current session state.
   */
  public void refresh() {
    Portfolio portfolio = session.getPlayer().getPortfolio();

    holdingsValue.setText(String.valueOf(portfolio.getShares().size()));
    totalInvestedValue.setText(portfolio.getTotalInvested().toPlainString());
    currentValueValue.setText(portfolio.getNetWorth().toPlainString());
    totalGainLossValue.setText(formatMovement(
        portfolio.getNetWorth().subtract(portfolio.getTotalInvested())));

    updateNetWorthChart(session.getNetWorthHistory());
    holdingsTable.getItems().setAll(portfolio.getShares());
  }

  private void updateNetWorthChart(List<BigDecimal> netWorthHistory) {
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
