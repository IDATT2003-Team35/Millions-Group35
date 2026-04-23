package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.view.components.SideBar;
import edu.ntnu.idi.idatt.millions.view.components.StatusBar;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * Main application shell. Keeps the status bar on top and the sidebar on the left,
 * while the center area can be swapped
 */
public class MainView extends BorderPane {

  private final StatusBar statusBar = new StatusBar();
  private final SideBar sideBar = new SideBar();

  public MainView() {
    setTop(statusBar);
    setLeft(sideBar);
  }

  /**
   * Replaces the center content with the given node.
   *
   * @param node the new content; must not be null
   */
  public void setContent(Node node) {
    if (node == null) {
      throw new IllegalArgumentException("Content node cannot be null");
    }
    setCenter(node);
  }

  public StatusBar getStatusBar() {
    return statusBar;
  }

  public SideBar getSideBar() {
    return sideBar;
  }
}
