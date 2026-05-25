package edu.ntnu.idi.idatt.millions.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a player's net worth history during a game session.
 *
 * <p>The history keeps one value per week. If a value is recorded multiple times for the same week,
 * the newest value replaces the old one.
 */
public class NetWorthHistory {
  private final List<BigDecimal> netWorthHistory;

  /** Creates an empty net worth history. */
  public NetWorthHistory() {
    this.netWorthHistory = new ArrayList<>();
  }

  /**
   * Creates a net worth history from saved values.
   *
   * @param history the saved net worth values
   * @throws IllegalArgumentException if history or any value in history is null
   */
  public NetWorthHistory(List<BigDecimal> history) {
    if (history == null) {
      throw new IllegalArgumentException("History cannot be null");
    }
    if (history.stream().anyMatch(value -> value == null)) {
      throw new IllegalArgumentException("History cannot contain null values");
    }
    this.netWorthHistory = new ArrayList<>(history);
  }

  /**
   * Records a net worth value for the given week.
   *
   * @param week the week number, starting at 1
   * @param netWorth the net worth to record
   * @throws IllegalArgumentException if week is less than 1 or netWorth is null
   */
  public void recordNewPoint(int week, BigDecimal netWorth) {
    if (week < 1) {
      throw new IllegalArgumentException("Week must be positive");
    }
    if (netWorth == null) {
      throw new IllegalArgumentException("Net worth cannot be null");
    }

    int weekIndex = week - 1;
    while (netWorthHistory.size() <= weekIndex) {
      netWorthHistory.add(BigDecimal.ZERO);
    }
    netWorthHistory.set(weekIndex, netWorth);
  }

  /**
   * Returns the recorded net worth history.
   *
   * @return a copy of the net worth history
   */
  public List<BigDecimal> getHistory() {
    return new ArrayList<>(netWorthHistory);
  }
}
