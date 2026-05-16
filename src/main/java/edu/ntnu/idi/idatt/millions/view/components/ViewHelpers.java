package edu.ntnu.idi.idatt.millions.view.components;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * Static helpers for building the styled UI elements shared by the
 * main views (market, portfolio, transactions).
 */
public final class ViewHelpers {

  private static final int ROW_HEIGHT = 52;
  private static final int HEADER_HEIGHT = 46;

  private ViewHelpers() {}

  /** Creates a large section title label (e.g. "MARKET", "PORTFOLIO"). */
  public static Label sectionTitle(String text) {
    Label label = new Label(text);
    label.getStyleClass().add("section-title");
    return label;
  }

  /** Wraps a table view in a StackPane with the dark border styling. */
  public static StackPane tableWrapper(Node table) {
    StackPane wrapper = new StackPane(table);
    wrapper.getStyleClass().add("table-wrapper");
    return wrapper;
  }

  /** Wraps content in a styled, page-level scroll pane. */
  public static ScrollPane pageScrollPane(Node content) {
    ScrollPane scrollPane = new ScrollPane(content);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.getStyleClass().add("portfolio-scroll");
    return scrollPane;
  }

  /**
   * Binds a table's preferred height to the number of items so the table
   * grows with its content instead of scrolling internally.
   */
  public static <T> void autoSizeTable(TableView<T> table) {
    table.setFixedCellSize(ROW_HEIGHT);
    table.prefHeightProperty().bind(
        Bindings.size(table.getItems()).multiply(ROW_HEIGHT).add(HEADER_HEIGHT));
  }

  /** Cell factory that displays a string with the "symbol-cell" style class. */
  public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> symbolCellFactory() {
    return col -> {
      TableCell<T, String> cell = new TableCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          setText(empty || item == null ? "" : item);
        }
      };
      cell.getStyleClass().add("symbol-cell");
      return cell;
    };
  }
}
