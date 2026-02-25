package edu.ntnu.idi.idatt.millions.calculator;

import java.math.BigDecimal;

/**
 * Defines operations to compute monetary values for a transaction.
 *
 * Implementations must return non-null {@link BigDecimal} results.
 *
 */
public interface TransactionCalculator {

  /**
   * Calculates the gross amount for the transaction.
   *
   * @return the gross amount as a non-null {@link BigDecimal}
   */
  BigDecimal calculateGross();

  /**
   * Calculates the commission applied to the transaction.
   *
   * @return the commission amount as a non-null {@link BigDecimal}
   */
  BigDecimal calculateCommission();

  /**
   * Calculates the tax applied to the transaction.
   *
   * @return the tax amount as a non-null {@link BigDecimal}
   */
  BigDecimal calculateTax();

  /**
   * Calculates the final total for the transaction, typically including gross,
   * commission and tax as defined by the implementation.
   *
   * @return the total amount as a non-null {@link BigDecimal}
   */
  BigDecimal calculateTotal();
}