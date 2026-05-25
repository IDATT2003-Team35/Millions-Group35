package edu.ntnu.idi.idatt.millions.util;

import java.math.BigDecimal;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

/** Helpers for building JavaFX {@link TableColumn}s. */
public final class TableColumns {

  private TableColumns() {}

  /**
   * Builds a column that sorts numerically on a {@link BigDecimal} extracted from each row, while
   * displaying a custom-formatted string in the cell. This lets a column header sort by value (e.g.
   * -10 below -5) instead of alphabetically (which would put "-10" before "-5") while keeping the
   * displayed text under the caller's control.
   *
   * @param title the column header text
   * @param extractor function that returns the numeric value for a row
   * @param formatter function that turns the numeric value into display text
   * @param <S> the row type
   * @return a configured numeric column
   */
  public static <S> TableColumn<S, BigDecimal> numericColumn(
      String title, Function<S, BigDecimal> extractor, Function<BigDecimal, String> formatter) {
    TableColumn<S, BigDecimal> col = new TableColumn<>(title);
    col.setCellValueFactory(c -> new SimpleObjectProperty<>(extractor.apply(c.getValue())));
    col.setCellFactory(
        c -> {
          TableCell<S, BigDecimal> cell =
              new TableCell<>() {
                @Override
                protected void updateItem(BigDecimal value, boolean empty) {
                  super.updateItem(value, empty);
                  setText(empty || value == null ? "" : formatter.apply(value));
                }
              };
          cell.getStyleClass().add("numeric-cell");
          return cell;
        });
    return col;
  }

  /**
   * Like {@link #numericColumn} but also applies a "gain" or "loss" CSS class to the cell based on
   * the sign of the value. Use for change-style columns where positive values should appear
   * green/blue and negative values red.
   *
   * @param title the column header text
   * @param extractor function that returns the numeric value for a row
   * @param formatter function that turns the numeric value into display text
   * @param <S> the row type
   * @return a configured numeric column with sign-based coloring
   */
  public static <S> TableColumn<S, BigDecimal> coloredNumericColumn(
      String title, Function<S, BigDecimal> extractor, Function<BigDecimal, String> formatter) {
    TableColumn<S, BigDecimal> col = new TableColumn<>(title);
    col.setCellValueFactory(c -> new SimpleObjectProperty<>(extractor.apply(c.getValue())));
    col.setCellFactory(
        c -> {
          TableCell<S, BigDecimal> cell =
              new TableCell<>() {
                @Override
                protected void updateItem(BigDecimal value, boolean empty) {
                  super.updateItem(value, empty);
                  getStyleClass().removeAll("gain", "loss");
                  if (empty || value == null) {
                    setText("");
                  } else {
                    setText(formatter.apply(value));
                    if (value.signum() > 0) {
                      getStyleClass().add("gain");
                    } else if (value.signum() < 0) {
                      getStyleClass().add("loss");
                    }
                  }
                }
              };
          cell.getStyleClass().add("numeric-cell");
          return cell;
        });
    return col;
  }
}
