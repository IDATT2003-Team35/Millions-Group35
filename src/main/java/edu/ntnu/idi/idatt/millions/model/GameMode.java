package edu.ntnu.idi.idatt.millions.model;

/**
 * Game mode defining the win condition and rules of a game session.
 *
 * <ul>
 *   <li>{@link #SANDBOX} unlimited play, editable starting capital,
 *       not eligible for highscore</li>
 *   <li>{@link #CHALLENGE} 52-week limit with automatic game-over,
 *       locked starting capital based on {@link Difficulty}, eligible
 *       for highscore</li>
 * </ul>
 */
public enum GameMode {
  SANDBOX(null),
  CHALLENGE(52);

  private final Integer weekLimit;

  GameMode(Integer weekLimit) {
    this.weekLimit = weekLimit;
  }

  /**
   * Returns the maximum number of weeks for this mode, or {@code null}
   * for unlimited play (SANDBOX).
   *
   * @return the week limit, or {@code null} if unlimited
   */
  public Integer getWeekLimit() {
    return weekLimit;
  }

  /**
   * Checks whether this mode has a fixed time limit that triggers
   * automatic game-over.
   *
   * @return {@code true} if the mode auto-ends at a fixed week,
   *         {@code false} otherwise
   */
  public boolean hasWeekLimit() {
    return weekLimit != null;
  }
}
