package edu.ntnu.idi.idatt.millions.controller;

import edu.ntnu.idi.idatt.millions.file.StockReader;
import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Stock;
import edu.ntnu.idi.idatt.millions.view.StartView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class StartController {
  private final StartView view;
  private final Stage stage;
  private final Consumer<GameSession> onGameStart;
  private final StockReader stockReader;
  private Path selectedFile;

  public StartController(StartView view, Stage stage, Consumer<GameSession> onGameStart, StockReader stockReader) {
    this.view = view;
    this.stage = stage;
    this.stockReader = stockReader;
    this.onGameStart = onGameStart;

    initialize();
  }

  private void initialize() {
    view.getBrowseButton().setOnAction(e -> handleBrowser());
    view.getStartButton().setOnAction(e -> handleStart());

  }

  private void handleBrowser() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose a stock CSV file");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV file (*.csv)", "*.csv"));

    var file = fileChooser.showOpenDialog(stage);
    if (file != null) {
      selectedFile = file.toPath();
      view.setSelectedFilePath(selectedFile.toString());
      view.clearErrorMessage();
    }
  }

  private void handleStart() {
    view.clearErrorMessage();

    String name = view.getNameField().getText().trim();
    if (name.isEmpty()) {
      view.setErrorMessage("Please enter name");
      return;
    }

    BigDecimal capital;
    try {
      capital = new BigDecimal(view.getCapitalField().getText().trim());
    } catch (NumberFormatException e) {
      view.setErrorMessage("Please enter a valid number");
      return;
    }

    if (capital.compareTo(BigDecimal.ZERO) <= 0) {
      view.setErrorMessage("Capital needs to be larger than 0");
      return;
    }

    if (selectedFile == null) {
      view.setErrorMessage("Please choose a CSV stock file");
      return;
    }

    if (!Files.exists(selectedFile)) {
      view.setErrorMessage("Selected file does not exist");
      return;
    }

    List<Stock> stocks;
    try {
      stocks = stockReader.readStockData(selectedFile);
    } catch (IOException | IllegalArgumentException e) {
      view.setErrorMessage("Could not read file: " + e.getMessage());
      return;
    }

    if (stocks.isEmpty()) {
      view.setErrorMessage("This file contains no stocks");
      return;
    }

    try {
      Player player = new Player(name, capital);
      Exchange exchange = new Exchange("Stock Exchange", stocks);
      GameSession session = new GameSession(player, exchange);
      onGameStart.accept(session);
    } catch (IllegalArgumentException e) {
      view.setErrorMessage(e.getMessage());
    }
  }
}
