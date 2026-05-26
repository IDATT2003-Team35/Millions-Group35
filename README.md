# Millions

Millions is a JavaFX stock trading simulator developed for IDATT2003 Programmering 2 at NTNU.

The application lets players buy and sell stocks, manage a portfolio, advance through simulated
trading weeks, and track their net worth over time. Stock prices change between weeks, and the game
supports both sandbox-style play and challenge-style play with difficulty-based settings.

## Requirements

- Java 25
- Maven 3.9

## Project Structure

```text
.
+-- pom.xml
+-- docs/
+-- src/
    +-- main/
    |   +-- java/edu/ntnu/idi/idatt/millions/
    |   |   +-- controller/       JavaFX controllers and screen flow
    |   |   +-- factory/          Transaction factory
    |   |   +-- file/             CSV and JSON file handling
    |   |   +-- model/            Domain model and game logic
    |   |   +-- observer/         Observer pattern interfaces
    |   |   +-- util/             Formatting, styling, and table helpers
    |   |   +-- view/             JavaFX views and reusable components
    |   |   +-- App.java          Application entry point
    |   +-- resources/            Stylesheet and default stock data
    +-- test/
        +-- java/                 Unit tests
        +-- resources/            Test stock data
```

## Features

- Start a new game with player name, starting capital, game mode, difficulty, and stock data
- Search, filter, and inspect stocks in a JavaFX market view
- View stock statistics, price history, top gainers, and top losers
- Buy and sell shares with transaction costs, tax handling, and receipt dialogs
- Display grouped portfolio holdings with gain/loss information
- Support partial sales using FIFO lots
- Save and load games as JSON
- Track completed transactions with filtering and receipt details
- Refresh the GUI through an observer-based update flow

## Build

Build and package the project:

```bash
mvn clean package
```

Run the full Maven verification lifecycle:

```bash
mvn verify
```

## Run

Start the JavaFX application:

```bash
mvn javafx:run
```

## Test

Run the unit tests:

```bash
mvn test
```

Generate the JaCoCo coverage report:

```bash
mvn verify
```

The coverage report is generated under `target/site/jacoco`.

## Javadocs

Generate Javadocs:

```bash
mvn javadoc:javadoc
```

The generated documentation is available under `target/reports/apidocs`.

## Authors

- Aksel Kirkhorn
- Petter Birklund-Jensen

IDATT2003 Group 35, NTNU, 2026.
