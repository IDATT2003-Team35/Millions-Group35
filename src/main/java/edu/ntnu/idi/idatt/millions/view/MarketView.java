package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.util.Percentages;
import edu.ntnu.idi.idatt.millions.util.TableColumns;
import edu.ntnu.idi.idatt.millions.view.components.FilterTabBar;
import edu.ntnu.idi.idatt.millions.view.components.ViewHelpers;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

/**
 * Market view showing all listed stocks with search, plus a side panel
 * with top gainers and losers. Refreshes itself when the game session changes.
 */
public class MarketView extends BorderPane implements Observer {

  private final Label titleLabel = ViewHelpers.sectionTitle("MARKET");
  private final TextField searchField = new TextField();
  private final Label instrumentCountLabel = new Label();
  private final FilterTabBar filterTabs = new FilterTabBar();
  {
    searchField.setPromptText("Search symbol or company");
    filterTabs.addTab("ALL", "ALL");
    filterTabs.addTab("GAINERS", "GAINERS");
    filterTabs.addTab("LOSERS", "LOSERS");
  }

  private final TableView<Stock> stockTable = new TableView<>();

  private final VBox gainersBox = new VBox();
  private final VBox losersBox = new VBox();
  private final VBox gainersPanel;
  private final VBox losersPanel;
  {
    gainersPanel = buildTopPanel("TOP GAINERS", gainersBox, "▲");
    losersPanel = buildTopPanel("TOP LOSERS", losersBox, "▼");
    gainersBox.getStyleClass().add("top-panel-body");
    losersBox.getStyleClass().add("top-panel-body");
  }

  private Runnable onUpdate = () -> { };
  private Consumer<Stock> onStockClick = stock -> { };

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

    setupColumns();
    buildLayout();
    session.addObserver(this);
  }

  private void buildLayout() {
    setPadding(new Insets(10));

    StackPane tableWrapper = ViewHelpers.tableWrapper(stockTable);
    tableWrapper.setMaxWidth(Double.MAX_VALUE);
    tableWrapper.setMaxHeight(Double.MAX_VALUE);

    VBox centerContent = new VBox(buildHeader(), tableWrapper);
    VBox.setVgrow(tableWrapper, Priority.ALWAYS);
    setCenter(centerContent);

    setRight(buildSidePanel());
  }

  private Node buildHeader() {
    HBox masthead = new HBox(titleLabel);
    masthead.setAlignment(Pos.CENTER_LEFT);

    HBox metaRow = new HBox(instrumentCountLabel);
    metaRow.getStyleClass().add("market-meta-row");

    Region filterSpacer = new Region();
    HBox.setHgrow(filterSpacer, Priority.ALWAYS);
    HBox filterRow = new HBox(filterTabs, filterSpacer, searchField);
    filterRow.setAlignment(Pos.CENTER_LEFT);
    filterRow.setSpacing(16);
    searchField.setPrefWidth(280);
    filterTabs.setOnSelectionChange(() -> onUpdate.run());

    VBox header = new VBox(masthead, metaRow, filterRow);
    header.setSpacing(10);
    header.setPadding(new Insets(0, 0, 14, 0));
    return header;
  }

  private Node buildSidePanel() {
    Region separator = new Region();
    separator.getStyleClass().add("side-separator");
    separator.setMaxHeight(Double.MAX_VALUE);

    VBox panels = new VBox(gainersPanel, losersPanel);
    panels.setSpacing(12);

    HBox container = new HBox(separator, panels);
    container.setSpacing(16);
    container.setPadding(new Insets(0, 0, 0, 16));
    return container;
  }

  private void setupColumns() {
    TableColumn<Stock, String> symbolCol = new TableColumn<>("SYMBOL");
    symbolCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getSymbol()));
    symbolCol.setCellFactory(ViewHelpers.symbolCellFactory());

    TableColumn<Stock, String> companyCol = new TableColumn<>("COMPANY");
    companyCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getCompany()));

    TableColumn<Stock, BigDecimal> priceCol = TableColumns.numericColumn(
        "PRICE", Stock::getSalesPrice, v -> Money.format(v).substring(1));

    TableColumn<Stock, BigDecimal> percentChangeCol = TableColumns.coloredNumericColumn(
        "CHANGE (%)", Stock::getLatestPercentChange, Percentages::formatWithArrow);
    TableColumn<Stock, BigDecimal> allTimeChangeCol = TableColumns.coloredNumericColumn(
        "ALL-TIME (%)", Stock::getTotalPercentChange, Percentages::formatWithArrow);

    stockTable.getColumns().addAll(
        symbolCol, companyCol, priceCol, percentChangeCol, allTimeChangeCol);
    symbolCol.setPrefWidth(90);
    companyCol.setPrefWidth(360);
    priceCol.setPrefWidth(140);
    percentChangeCol.setPrefWidth(150);
    allTimeChangeCol.setPrefWidth(150);
    stockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
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
   * Registers a handler invoked when the user clicks a stock entry in the
   * top gainers or top losers side panels.
   *
   * @param handler the consumer that receives the clicked stock; must not be null
   * @throws IllegalArgumentException if handler is null
   */
  public void setOnStockClick(Consumer<Stock> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    this.onStockClick = handler;
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
   * Replaces the entries shown in the Top Gainers side panel.
   *
   * @param stocks the gaining stocks to list
   */
  public void setGainers(List<Stock> stocks) {
    populateTopList(gainersBox, stocks, "gain");
  }

  /**
   * Replaces the entries shown in the Top Losers side panel.
   *
   * @param stocks the losing stocks to list
   */
  public void setLosers(List<Stock> stocks) {
    populateTopList(losersBox, stocks, "loss");
  }

  private void populateTopList(VBox box, List<Stock> stocks, String changeClass) {
    box.getChildren().clear();
    for (Stock stock : stocks) {
      Label symbol = new Label(stock.getSymbol());
      symbol.getStyleClass().add("top-panel-symbol");
      Label change = new Label(Percentages.formatWithArrow(stock.getLatestPercentChange()));
      change.getStyleClass().addAll("top-panel-change", changeClass);
      Region spacer = new Region();
      HBox.setHgrow(spacer, Priority.ALWAYS);
      HBox row = new HBox(symbol, spacer, change);
      row.getStyleClass().add("top-panel-row");
      row.setOnMouseClicked(e -> onStockClick.accept(stock));
      box.getChildren().add(row);
    }
  }

  public TextField getSearchField() {
    return searchField;
  }

  public TableView<Stock> getStockTable() {
    return stockTable;
  }

  public String getSelectedFilter() {
    return filterTabs.getSelectedCode();
  }

  public void setFilterCounts(int all, int gainers, int losers) {
    filterTabs.setCounts(Map.of("ALL", all, "GAINERS", gainers, "LOSERS", losers));
  }

  public void setInstrumentCount(int count) {
    instrumentCountLabel.setText(count + " INSTRUMENTS");
  }

  private VBox buildTopPanel(String title, VBox body, String indicator) {
    Label header;
    if (indicator != null) {
      header = new Label(indicator + "  " + title);
    } else {
      header = new Label(title);
    }

    Label symLabel = new Label("SYM");
    Label chgLabel = new Label("CHG");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox headerRow = new HBox(symLabel, spacer, chgLabel);
    headerRow.getStyleClass().add("top-panel-header");

    VBox panel = new VBox(header, headerRow, body);
    panel.getStyleClass().add("top-panel");
    return panel;
  }
}
