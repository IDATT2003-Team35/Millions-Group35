package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.file.save.GameSaveException;
import edu.ntnu.idi.idatt.millions.file.save.GameSaveService;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.view.LoadGameView;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/** Controller for loading saved games. */
public class LoadGameController {
  private final LoadGameView view;
  private final GameSaveService saveService;
  private final Consumer<GameSession> onGameLoaded;
  private final Runnable onBack;

  /**
   * Creates a load game controller and wires its actions.
   *
   * @param view the load game view
   * @param saveService the service used to list and load save files
   * @param onGameLoaded callback invoked when a game has been loaded
   * @param onBack callback invoked when the user wants to return to the start menu
   * @throws IllegalArgumentException if any argument is null
   */
  public LoadGameController(
      LoadGameView view,
      GameSaveService saveService,
      Consumer<GameSession> onGameLoaded,
      Runnable onBack) {
    if (view == null) {
      throw new IllegalArgumentException("view cannot be null");
    }
    if (saveService == null) {
      throw new IllegalArgumentException("saveService cannot be null");
    }
    if (onGameLoaded == null) {
      throw new IllegalArgumentException("onGameLoaded cannot be null");
    }
    if (onBack == null) {
      throw new IllegalArgumentException("onBack cannot be null");
    }

    this.view = view;
    this.saveService = saveService;
    this.onGameLoaded = onGameLoaded;
    this.onBack = onBack;

    initialize();
  }

  private void initialize() {
    view.getBackButton().setOnAction(e -> onBack.run());
    view.getLoadButton().setOnAction(e -> handleLoad());
    loadSaveFiles();
  }

  private void loadSaveFiles() {
    try {
      List<Path> saveFiles = saveService.listSaveFiles();
      view.setSaveFiles(saveFiles);
      if (saveFiles.isEmpty()) {
        view.setErrorMessage("No saved games found.");
      } else {
        view.clearErrorMessage();
      }
    } catch (GameSaveException e) {
      view.setErrorMessage(e.getMessage());
    }
  }

  private void handleLoad() {
    view.clearErrorMessage();
    Path selectedSave = view.getSaveListView().getSelectionModel().getSelectedItem();
    if (selectedSave == null) {
      view.setErrorMessage("Please choose a saved game.");
      return;
    }

    try {
      GameSession session = saveService.load(selectedSave);
      onGameLoaded.accept(session);
    } catch (GameSaveException e) {
      view.setErrorMessage(e.getMessage());
    }
  }
}
