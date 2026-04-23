package edu.ntnu.idi.idatt.millions.observer;

/**
 * Subject interface for objects that can be observed for state changes.
 */
public interface Subject {

  /**
   * Registers an observer.
   *
   * @param observer the observer to add
   */
  void addObserver(Observer observer);

  /**
   * Removes an observer.
   *
   * @param observer the observer to remove
   */
  void removeObserver(Observer observer);

  /**
   * Notifies all registered observers about a state change.
   */
  void notifyObservers();
}
