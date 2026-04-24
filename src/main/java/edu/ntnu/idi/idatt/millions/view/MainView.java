package edu.ntnu.idi.idatt.millions.view;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.observer.Observer;
import edu.ntnu.idi.idatt.millions.view.components.SideBar;
import edu.ntnu.idi.idatt.millions.view.components.StatusBar;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Main application shell. Keeps the status bar on top and the sidebar on the left,
 * while the center area is swapped via {@link #showContent(Node, Button)}. Observes
 * the game session and refreshes its display whenever model state changes.
 */
public class MainView extends BorderPane implements Observer {

  private final GameSession session;
  private final StatusBar statusBar = new StatusBar();
  private final SideBar sideBar = new SideBar();

  /**
   * Builds the main shell and registers as an observer on the session.
   *
   * @param session the active game session; must not be null
   * @throws IllegalArgumentException if session is null
   */
  public MainView(GameSession session) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    this.session = session;

    buildLayout();
    session.addObserver(this);
    refresh();
  }

  private void buildLayout() {
    setTop(buildHeader());
    setLeft(buildLeftPane());
  }

  private Node buildHeader() {
    return new VBox(statusBar, new Separator());
  }

  private Node buildLeftPane() {
    return new HBox(sideBar, new Separator(Orientation.VERTICAL));
  }

  @Override
  public void update() {
    refresh();
  }

  /**
   * Replaces the center content and highlights the corresponding nav button.
   *
   * @param content the new center node; must not be null
   * @param activeButton the sidebar button to mark as active
   */
  public void showContent(Node content, Button activeButton) {
    if (content == null) {
      throw new IllegalArgumentException("Content cannot be null");
    }
    setCenter(content);
    markActive(activeButton);
  }

  public StatusBar getStatusBar() {
    return statusBar;
  }

  public SideBar getSideBar() {
    return sideBar;
  }

  private void refresh() {
    statusBar.refresh(session.getPlayer(), session.getExchange());
  }

  private void markActive(Button active) {
    for (Button b : new Button[] {
        sideBar.getMarketButton(),
        sideBar.getPortfolioButton(),
        sideBar.getTransactionButton()}) {
      b.setStyle(b == active ? "-fx-font-weight: bold;" : "");
    }
  }
}
