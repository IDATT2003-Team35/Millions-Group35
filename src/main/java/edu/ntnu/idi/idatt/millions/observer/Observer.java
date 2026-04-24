package edu.ntnu.idi.idatt.millions.observer;

/**
 * Observer interface for components that should react to game state changes.
 */
public interface Observer {

  /**
   * Called when the observed subject changes state.
   */
  void update();
}
