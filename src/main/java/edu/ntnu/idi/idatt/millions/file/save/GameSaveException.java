package edu.ntnu.idi.idatt.millions.file.save;

/**
 * Exception thrown when saving or loading game data fails.
 */
public class GameSaveException extends Exception {

  /**
   * Creates a new game save exception with a descriptive error message.
   *
   * @param message description of the save or load failure
   */
  public GameSaveException(String message) {
    super(message);
  }

  /**
   * Creates a new game save exception with a descriptive error message and cause.
   *
   * @param message description of the save or load failure
   * @param cause the original exception that caused the failure
   */
  public GameSaveException(String message, Throwable cause) {
    super(message, cause);
  }
}
