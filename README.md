# Chess-Like Game Project

## Overview
This project implements a customizable chess-like game. The system provides a foundation for board-based strategy games with various pieces, players, and game rules. It includes a graphical user interface (GUI) for ease of interaction and testing. The project is written in Java and consists of multiple components designed to work together seamlessly.

## Features
- **Customizable Game Logic**: Allows users to define new pieces and rules.
- **Graphical User Interface (GUI)**: Provides a user-friendly interface for playing the game.
- **Extensible Design**: Uses object-oriented programming principles, enabling easy extension for new game types.
- **JUnit Tests**: Ensures the reliability of core game logic through comprehensive unit tests.
- **Interactive Gameplay**: Supports multiple players with dynamic turn-based gameplay.

## File Structure
The project is organized into the following key files:

### **Core Logic**
- **`Position.java`**: Handles the positions on the game board, including coordinates and validity checks.
- **`Piece.java`**: Abstract class defining the general behavior of a piece.
- **`ConcretePiece.java`**: Implements specific pieces' functionality, serving as the base for more concrete pieces like King or Pawn.
- **`King.java`**: Implements the logic for the King piece, including its movement rules.
- **`Pawn.java`**: Implements the logic for the Pawn piece, including its unique movement and promotion rules.
- **`PlayableLogic.java`**: Interfaces or base logic for defining playability within the game.

### **Player Management**
- **`Player.java`**: Abstract class that defines a player in the game.
- **`ConcretePlayer.java`**: Concrete implementation of a player, managing resources like pieces and turns.

### **Game Management**
- **`GameLogic.java`**: Core logic for handling game rules, turn progression, and victory conditions.
- **`GameLogicTest.java`**: Unit tests for validating the logic implemented in `GameLogic`.

### **User Interface**
- **`GUI_for_chess_like_games.java`**: The graphical user interface for the game. Allows users to interact with the game visually.

### **Execution**
- **`Main.java`**: Entry point for the application. Initializes the game and sets up the environment.

## Requirements
- **Java Development Kit (JDK)** 17 or later
- **JUnit** for testing (included in most Java IDEs)
