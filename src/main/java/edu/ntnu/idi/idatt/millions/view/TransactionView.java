package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.transaction.Purchase;
import edu.ntnu.idi.idatt.millions.model.transaction.Transaction;
import edu.ntnu.idi.idatt.millions.model.transaction.TransactionArchive;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Transaction view showing the player's completed transactions in a searchable
 * table. Refreshes itself when the game session changes.
 */
public class TransactionView extends BorderPane implements Observer {

  private final GameSession session;

  private final Label titleLabel = new Label("TRANSACTION HISTORY");

  private final TextField searchField = new TextField();

  private final TableView<Transaction> transactionTable = new TableView<>();

  private Consumer<Transaction> transactionHandler = transaction -> { };

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
    refresh();
  }

  private void buildLayout() {
    setPadding(new Insets(10));
    setTop(buildHeader());
    setCenter(buildContent());
  }

  private Node buildHeader() {
    HBox searchRow = new HBox(new Label("Search:"), searchField);
    searchRow.setSpacing(8);
    Separator separator = new Separator();
    VBox header = new VBox(titleLabel, separator, searchRow);
    header.setSpacing(8);
    header.setPadding(new Insets(0, 0, 10, 0));
    return header;
  }

  private Node buildContent() {
    VBox content = new VBox(transactionTable);
    content.setSpacing(10);
    VBox.setVgrow(transactionTable, Priority.ALWAYS);
    return content;
  }


  private void setupColumns() {
    TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue() instanceof Purchase ? "BUY" : "SELL"));

    TableColumn<Transaction, String> symbolCol = new TableColumn<>("Symbol");
    symbolCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getShare().getStock().getSymbol()));

    TableColumn<Transaction, String> qtyCol = new TableColumn<>("Qty");
    qtyCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getShare().getQuantity().toPlainString()));

    TableColumn<Transaction, String> totalCol = new TableColumn<>("Total ($)");
    totalCol.setCellValueFactory(c ->
        new SimpleStringProperty(c.getValue().getCalculator().calculateGross().toPlainString()));

    TableColumn<Transaction, String> weekCol = new TableColumn<>("Week");
    weekCol.setCellValueFactory(c ->
        new SimpleStringProperty(String.valueOf(c.getValue().getWeek())));

    TableColumn<Transaction, Void> actionCol = new TableColumn<>("Action");
    actionCol.setCellFactory(col -> new TableCell<Transaction, Void>() {
      private final Button viewTransactionButton = new Button("Show Receipt");
      {
        viewTransactionButton.setOnAction(e -> {
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

    transactionTable.getColumns().addAll(
        typeCol, symbolCol, qtyCol, totalCol, weekCol, actionCol);
  }

  @Override
  public void update() {
    refresh();
  }

  /**
   * Refreshes the transaction table from the current session state.
   */
  public void refresh() {
    TransactionArchive transactions = session.getPlayer().getTransactionArchive();
    transactionTable.getItems().setAll(transactions.getAll());
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
}
