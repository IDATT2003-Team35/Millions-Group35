package edu.ntnu.idi.idatt.millions;

import edu.ntnu.idi.idatt.millions.controller.MainController;
import edu.ntnu.idi.idatt.millions.file.StockReader;
import edu.ntnu.idi.idatt.millions.model.Exchange;
import edu.ntnu.idi.idatt.millions.model.GameSession;
import edu.ntnu.idi.idatt.millions.model.Player;
import edu.ntnu.idi.idatt.millions.model.Stock;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point for the Millions JavaFX application.
 */
public class App extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    GameSession session = createDummySession();
    MainController controller = new MainController(session);

    Scene scene = new Scene(controller.getView(), 1280, 800);
    stage.setTitle("Millions");
    stage.setScene(scene);
    stage.show();
  }

  private GameSession createDummySession() throws IOException{
    List<Stock> stocks = new StockReader()
        .readStockData(Path.of("src/main/resources/sp500.csv"));

    Player player = new Player("Player", new BigDecimal("10000"));
    Exchange exchange = new Exchange("NASDAQ", stocks);
    return new GameSession(player, exchange);
  }

  public static void main(String[] args) {
    launch(args);
  }
}
