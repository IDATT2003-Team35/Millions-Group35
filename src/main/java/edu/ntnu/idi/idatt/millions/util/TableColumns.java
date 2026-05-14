package edu.ntnu.idi.idatt.millions.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Helpers for building JavaFX {@link TableColumn}s.
 */
public final class TableColumns {

  private TableColumns() {}

  /**
   * Builds a column that sorts numerically on a {@link BigDecimal} extracted from
   * each row, while displaying a custom-formatted string in the cell. This lets
   * a column header sort by value (e.g. -10 below -5) instead of alphabetically
   * (which would put "-10" before "-5") while keeping the displayed text under
   * the caller's control.
   *
   * @param title     the column header text
   * @param extractor function that returns the numeric value for a row
   * @param formatter function that turns the numeric value into display text
   * @param <S>       the row type
   * @return a configured numeric column
   */
  public static <S> TableColumn<S, BigDecimal> numericColumn(
      String title,
      Function<S, BigDecimal> extractor,
      Function<BigDecimal, String> formatter) {
    TableColumn<S, BigDecimal> col = new TableColumn<>(title);
    col.setCellValueFactory(c ->
        new SimpleObjectProperty<>(extractor.apply(c.getValue())));
    col.setCellFactory(c -> new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal value, boolean empty) {
        super.updateItem(value, empty);
        setText(empty || value == null ? "" : formatter.apply(value));
      }
    });
    return col;
  }
}
