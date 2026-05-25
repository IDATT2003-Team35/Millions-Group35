package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.model.GameSession;
import javafx.scene.Node;

/**
 * Base class for controllers that present a tab in the main view.
 *
 * <p>Each page controller holds the active {@link GameSession} and a reference
 * to the parent {@link MainController}, and exposes a JavaFX root node so the
 * parent can place it inside the main layout. Subclasses build their own view
 * and wire event handlers in their own constructor.</p>
 */
public abstract class PageController {

  protected final GameSession session;
  protected final MainController mainController;

  protected PageController(GameSession session, MainController mainController) {
    if (session == null) {
      throw new IllegalArgumentException("Session cannot be null");
    }
    if (mainController == null) {
      throw new IllegalArgumentException("MainController cannot be null");
    }
    this.session = session;
    this.mainController = mainController;
  }

  /**
   * Returns the JavaFX root node for this controller's view. Implementations
   * typically return a view class that extends a layout pane.
   *
   * @return the view root node
   */
  public abstract Node getView();
}
