# Iro Chess Gui
A playable chess app built in Java Swing
The engine is built in c in my iro chess repo
https://github.com/Irokanade/iro-chess

## How to play
Drag and drop the pieces to move them

### Prerequisites
- Java 21 or higher installed

### Running the Game
By default, the engine plays as Black if no argument is provided.
```bash
./gradlew run
```
To start the game with the engine playing **as White**:
```bash
./gradlew run --args="white"
```

## Screenshots
![screenshot-1](screenshots/iro-chess-gui-screenshot-1.png)
*The main chess interface*

## Engines
The default uses my custom engine, 
if you want to use your own engine you may add the executables to the `/engines` folder under your current os
