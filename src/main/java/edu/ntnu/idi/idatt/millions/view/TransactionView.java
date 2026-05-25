package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.util.Money;
import edu.ntnu.idi.idatt.millions.view.components.FilterTabBar;
import edu.ntnu.idi.idatt.millions.view.components.ViewHelpers;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Transaction view showing the player's completed transactions in a searchable table. Refreshes
 * itself when the game session changes.
 */
public class TransactionView extends BorderPane implements Observer {

  private final GameSession session;

  private final Label titleLabel = ViewHelpers.sectionTitle("TRANSACTIONS");

  private final TextField searchField = new TextField();

  private final Label totalBoughtValue = new Label();
  private final Label totalBoughtSubtitle = new Label();
  private final Label totalSoldValue = new Label();
  private final Label totalSoldSubtitle = new Label();
  private final Label netActivityValue = new Label();
  private final Label recordsValue = new Label();

  private final FilterTabBar filterTabs = new FilterTabBar();

  {
    filterTabs.addTab("ALL", "ALL");
    filterTabs.addTab("BUYS", "BUYS");
    filterTabs.addTab("SELLS", "SELLS");
  }

  private final TableView<Transaction> transactionTable = new TableView<>();

  private Consumer<Transaction> transactionHandler = transaction -> {};
  private Runnable onUpdate = () -> {};

  /**
   * Creates a new transaction view bound to the given game session.
   *
   * @param session the active game session, must not be null
   * @throws IllegalArgumentException if session is null
   */
  public TransactionView(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    this.session = session;

    setupColumns();
    buildLayout();
    session.addObserver(this);
  }

  private void buildLayout() {
    setPadding(new Insets(10));
    searchField.setPromptText("Filter by symbol, type, or week");
    searchField.setPrefWidth(280);

    filterTabs.setOnSelectionChange(() -> onUpdate.run());

    Region filterSpacer = new Region();
    HBox.setHgrow(filterSpacer, Priority.ALWAYS);
    HBox filterRow = new HBox(filterTabs, filterSpacer, searchField);
    filterRow.setAlignment(Pos.CENTER_LEFT);

    ViewHelpers.autoSizeTable(transactionTable);

    VBox content =
        new VBox(
            titleLabel, buildSummaryBar(), filterRow, ViewHelpers.tableWrapper(transactionTable));
    content.setSpacing(14);

    setCenter(ViewHelpers.pageScrollPane(content));
  }

  private Node buildSummaryBar() {
    HBox bar =
        new HBox(
            buildSummaryBox("TOTAL BOUGHT", totalBoughtValue, totalBoughtSubtitle),
            buildSummaryBox("TOTAL SOLD", totalSoldValue, totalSoldSubtitle),
            buildSummaryBox("NET ACTIVITY", netActivityValue, new Label("Capital deployed")),
            buildSummaryBox("RECORDS", recordsValue, new Label("All time")));
    bar.getStyleClass().add("summary-bar");
    return bar;
  }

  private Node buildSummaryBox(String title, Label valueLabel, Label subtitleLabel) {
    Label titleLabelLocal = new Label(title);
    titleLabelLocal.getStyleClass().add("summary-title");
    valueLabel.getStyleClass().add("summary-value");
    subtitleLabel.getStyleClass().add("summary-subtitle");
    VBox box = new VBox(titleLabelLocal, valueLabel, subtitleLabel);
    box.getStyleClass().add("summary-box");
    HBox.setHgrow(box, Priority.ALWAYS);
    return box;
  }

  private void setupColumns() {
    TableColumn<Transaction, String> typeCol = new TableColumn<>("TYPE");
    typeCol.setCellValueFactory(
        c -> new SimpleStringProperty(c.getValue() instanceof Purchase ? "BUY" : "SELL"));
    typeCol.setCellFactory(
        c ->
            new TableCell<>() {
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("gain", "loss");
                if (empty || item == null) {
                  setText("");
                } else if ("BUY".equals(item)) {
                  setText("▲ BUY");
                  getStyleClass().add("gain");
                } else {
                  setText("▼ SELL");
                  getStyleClass().add("loss");
                }
              }
            });

    TableColumn<Transaction, String> symbolCol = new TableColumn<>("SYMBOL");
    symbolCol.setCellValueFactory(
        c -> new SimpleStringProperty(c.getValue().getShare().getStock().getSymbol()));
    symbolCol.setCellFactory(ViewHelpers.symbolCellFactory());

    TableColumn<Transaction, String> qtyCol = new TableColumn<>("QTY");
    qtyCol.setCellValueFactory(
        c -> new SimpleStringProperty(c.getValue().getShare().getQuantity().toPlainString()));

    TableColumn<Transaction, String> totalCol = new TableColumn<>("TOTAL ($)");
    totalCol.setCellValueFactory(
        c -> {
          BigDecimal gross = c.getValue().getCalculator().calculateGross();
          return new SimpleStringProperty(Money.format(gross).substring(1));
        });

    TableColumn<Transaction, String> weekCol = new TableColumn<>("WEEK");
    weekCol.setCellValueFactory(c -> new SimpleStringProperty("W" + c.getValue().getWeek()));
    weekCol.setCellFactory(
        c -> {
          TableCell<Transaction, String> cell =
              new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                  super.updateItem(item, empty);
                  setText(empty || item == null ? "" : item);
                }
              };
          cell.getStyleClass().add("week-cell");
          return cell;
        });

    TableColumn<Transaction, Void> actionCol = new TableColumn<>("");
    actionCol.setCellFactory(
        col ->
            new TableCell<Transaction, Void>() {
              private final Button viewTransactionButton = new Button("RECEIPT");

              {
                viewTransactionButton.setOnAction(
                    e -> {
                      Transaction t = getTableView().getItems().get(getIndex());
                      transactionHandler.accept(t);
                    });
              }

              @Override
              protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewTransactionButton);
              }
            });

    transactionTable.getColumns().addAll(typeCol, symbolCol, qtyCol, totalCol, weekCol, actionCol);
    transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
  }

  @Override
  public void update() {
    onUpdate.run();
  }

  /**
   * Registers a callback invoked when the view receives an Observer update. The controller uses
   * this to re-apply its current filter and push fresh data.
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
   * Replaces the transactions currently shown in the table and re-applies any active column sort so
   * the user's chosen order persists across updates.
   *
   * @param transactions the transactions to display
   */
  public void setTransactions(List<Transaction> transactions) {
    transactionTable.getItems().setAll(transactions);
    transactionTable.sort();
  }

  /**
   * Sets the handler invoked when the Show Receipt button on a row is clicked.
   *
   * @param handler the consumer to receive the clicked transaction; must not be null
   * @throws IllegalArgumentException if handler is null
   */
  public void setViewTransactionHandler(Consumer<Transaction> handler) {
    if (handler == null) {
      throw new IllegalArgumentException("Handler cannot be null");
    }
    this.transactionHandler = handler;
  }

  public TextField getSearchField() {
    return searchField;
  }

  public TableView<Transaction> getTransactionTable() {
    return transactionTable;
  }

  public String getSelectedFilter() {
    return filterTabs.getSelectedCode();
  }

  public void setFilterCounts(int all, int buys, int sells) {
    filterTabs.setCounts(Map.of("ALL", all, "BUYS", buys, "SELLS", sells));
  }

  public void setTotalBought(String value, int orderCount) {
    totalBoughtValue.setText(value);
    totalBoughtSubtitle.setText(orderCount + " buy orders");
  }

  public void setTotalSold(String value, int orderCount) {
    totalSoldValue.setText(value);
    totalSoldSubtitle.setText(orderCount + " sell orders");
  }

  public void setNetActivity(String value) {
    netActivityValue.setText(value);
  }

  public void setRecordsCount(int count) {
    recordsValue.setText(String.valueOf(count));
  }
}
