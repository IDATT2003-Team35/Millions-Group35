package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.observer.Observer;
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

  private static final int TOP_LIST_LIMIT = 5;

  private final GameSession session;

  private final Label titleLabel = new Label("MARKET");
  private final TextField searchField = new TextField();
  private final Label countLabel = new Label();

  private final TableView<Stock> stockTable = new TableView<>();

  private final VBox gainersBox = new VBox();
  private final VBox losersBox = new VBox();

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
    refreshTable();
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

    TableColumn<Stock, String> priceCol = new TableColumn<>("Price ($)");
    priceCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getSalesPrice().toPlainString()));

    TableColumn<Stock, String> changeCol = new TableColumn<>("Change ($)");
    changeCol.setCellValueFactory(c ->
        new SimpleStringProperty(formatChange(c.getValue().getLatestPriceChange())));

    stockTable.getColumns().addAll(symbolCol, companyCol, priceCol, changeCol);
  }

  @Override
  public void update() {
    refreshTable();
  }

  /**
   * Refreshes the stock table, gainers, losers and the count label
   * from the current state of the session's exchange.
   */
  public void refreshTable() {
    List<Stock> allStocks = session.getExchange().getStocks();
    stockTable.getItems().setAll(allStocks);
    countLabel.setText("Showing " + allStocks.size() + " of " + allStocks.size() + " stocks");

    refreshTopList(gainersBox, session.getExchange().getGainers(TOP_LIST_LIMIT));
    refreshTopList(losersBox, session.getExchange().getLosers(TOP_LIST_LIMIT));
  }

  private void refreshTopList(VBox box, List<Stock> stocks) {
    box.getChildren().clear();
    for (Stock stock : stocks) {
      String text = stock.getSymbol() + "  " + formatChange(stock.getLatestPriceChange());
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
