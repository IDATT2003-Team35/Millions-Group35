package edu.ntnu.idi.idatt.millions.view.components;

import java.util.LinkedHashMap;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

/**
 * Row of filter toggle tabs (e.g. ALL/GAINERS/LOSERS). Each tab carries a label and a small count
 * badge; selection state is exposed via a code supplied when building the bar.
 */
public class FilterTabBar extends HBox {

  private final ToggleGroup group = new ToggleGroup();
  private final Map<String, ToggleButton> tabsByCode = new LinkedHashMap<>();
  private final Map<String, Label> badgesByCode = new LinkedHashMap<>();
  private String defaultCode;

  /** Creates an empty filter tab bar with single-selection behavior. */
  public FilterTabBar() {
    setAlignment(Pos.CENTER_LEFT);
    setSpacing(0);

    group
        .selectedToggleProperty()
        .addListener(
            (obs, oldT, newT) -> {
              if (newT == null && defaultCode != null) {
                tabsByCode.get(defaultCode).setSelected(true);
              }
            });
  }

  /**
   * Adds a tab to the bar. The first tab added gets the rounded-edge styling and is selected by
   * default.
   *
   * @param code internal identifier returned by {@link #getSelectedCode()}
   * @param label tab text shown to the user
   */
  public void addTab(String code, String label) {
    Label badge = new Label();
    badge.getStyleClass().add("filter-count-badge");
    Label textLabel = new Label(label);
    textLabel.getStyleClass().add("filter-tab-text");

    HBox content = new HBox(6, textLabel, badge);
    content.setAlignment(Pos.CENTER);

    ToggleButton tab = new ToggleButton();
    tab.setGraphic(content);
    tab.setToggleGroup(group);
    tab.getStyleClass().add("filter-tab");

    if (tabsByCode.isEmpty()) {
      tab.getStyleClass().add("filter-tab-first");
      tab.setSelected(true);
      defaultCode = code;
    }

    tabsByCode.put(code, tab);
    badgesByCode.put(code, badge);
    getChildren().add(tab);
  }

  /**
   * Sets the badge counts. Map keys must match the codes used in {@link #addTab}.
   *
   * @param counts map from tab code to visible badge count
   */
  public void setCounts(Map<String, Integer> counts) {
    counts.forEach(
        (code, count) -> {
          Label badge = badgesByCode.get(code);
          if (badge != null) {
            badge.setText(String.valueOf(count));
          }
        });
  }

  /**
   * Returns the code of the currently selected tab, or the default if none.
   *
   * @return selected tab code
   */
  public String getSelectedCode() {
    Toggle selected = group.getSelectedToggle();
    for (Map.Entry<String, ToggleButton> entry : tabsByCode.entrySet()) {
      if (entry.getValue() == selected) {
        return entry.getKey();
      }
    }
    return defaultCode;
  }

  /**
   * Registers a listener invoked whenever the selected tab changes.
   *
   * @param callback callback to run when the selected tab changes
   */
  public void setOnSelectionChange(Runnable callback) {
    group
        .selectedToggleProperty()
        .addListener(
            (obs, oldT, newT) -> {
              if (newT != null) {
                callback.run();
              }
            });
  }
}
