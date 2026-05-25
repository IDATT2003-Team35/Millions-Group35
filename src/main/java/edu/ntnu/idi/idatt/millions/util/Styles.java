package edu.ntnu.idi.idatt.millions.util;

import javafx.scene.Scene;

/**
 * Helpers for attaching the project stylesheet to JavaFX scenes.
 *
 * <p>Popup scenes do not inherit stylesheets from the main scene, so any scene that should look
 * like the rest of the application must explicitly load {@code /styles.css}. This class centralizes
 * that loading so the stylesheet path lives in one place.
 */
public final class Styles {

  /** Classpath location of the project stylesheet. */
  private static final String MAIN_STYLESHEET = "/styles.css";

  private Styles() {}

  /**
   * Adds the project stylesheet to the given scene if it is not already present. Safe to call
   * multiple times.
   *
   * @param scene the scene to style; if {@code null} this is a no-op
   */
  public static void applyTo(Scene scene) {
    if (scene == null) {
      return;
    }
    String css = Styles.class.getResource(MAIN_STYLESHEET).toExternalForm();
    if (!scene.getStylesheets().contains(css)) {
      scene.getStylesheets().add(css);
    }
  }
}
