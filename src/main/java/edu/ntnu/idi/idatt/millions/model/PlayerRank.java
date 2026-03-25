package edu.ntnu.idi.idatt.millions.model;

/**
 * Represents the progression levels or statuses a player can achieve
 * based on their trading history and net worth growth.
 */
public enum PlayerRank {
  /**
   * The starting level for all players. No specific trading or profit requirements.
   */
  NOVICE,

  /**
   * Achieved by players who have traded for at least 10 distinct weeks
   * and have increased their net worth by at least 20%.
   */
  INVESTOR,

  /**
   * Achieved by players who have traded for at least 20 distinct weeks
   * and have at least doubled their initial net worth.
   */
  SPECULATOR,

  /**
   * The absolute pinnacle of stock market domination. Achieved only by legendary
   * traders who break the very fabric of the economy.
   */
  xXGODTIERMASTERSIXSEVENXx
}
