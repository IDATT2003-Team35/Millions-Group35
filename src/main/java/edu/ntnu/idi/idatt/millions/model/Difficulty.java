package edu.ntnu.idi.idatt.millions.model;

import java.math.BigDecimal;

/**
 * Difficulty level for a game session. Determines the player's default starting capital and the
 * market volatility.
 *
 * <p>In {@link GameMode#SANDBOX} the player may override the default starting capital, but in
 * {@link GameMode#CHALLENGE} the capital is locked to the difficulty's default value to ensure fair
 * highscore comparison across players.
 *
 * <p>Volatility values are weekly standard deviations of log returns:
 *
 * <ul>
 *   <li>EASY calm market, more forgiving
 *   <li>NORMAL balanced default
 *   <li>HARD turbulent market with low starting capital
 * </ul>
 */
public enum Difficulty {
  /** Forgiving difficulty with high starting capital and low market volatility. */
  EASY(new BigDecimal("10000"), 0.05),

  /** Balanced difficulty with medium starting capital and market volatility. */
  NORMAL(new BigDecimal("5000"), 0.10),

  /** Challenging difficulty with low starting capital and high market volatility. */
  HARD(new BigDecimal("1000"), 0.20);

  private final BigDecimal defaultStartingCapital;
  private final double volatility;

  Difficulty(BigDecimal defaultStartingCapital, double volatility) {
    this.defaultStartingCapital = defaultStartingCapital;
    this.volatility = volatility;
  }

  /**
   * Returns the default starting capital for this difficulty. Used as the initial value in the
   * new-game form; locked in {@link GameMode#CHALLENGE} mode.
   *
   * @return the default starting capital
   */
  public BigDecimal getDefaultStartingCapital() {
    return defaultStartingCapital;
  }

  /**
   * Returns the weekly volatility (σ) for the GBM price simulation.
   *
   * @return the weekly volatility as a decimal (e.g. 0.10 = 10%)
   */
  public double getVolatility() {
    return volatility;
  }
}
