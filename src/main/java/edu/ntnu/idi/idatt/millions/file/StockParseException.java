package edu.ntnu.idi.idatt.millions.file;

/**
 * Exception thrown when stock data cannot be parsed from a CSV file.
 */
public class StockParseException extends Exception {

  /**
   * Creates a new stock parse exception with a descriptive error message.
   *
   * @param message description of the parse failure
   */
  public StockParseException(String message) {
    super(message);
  }

  /**
   * Creates a new stock parse exception with a descriptive error message and cause.
   *
   * @param message description of the parse failure
   * @param cause the original exception that caused the failure
   */
  public StockParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
