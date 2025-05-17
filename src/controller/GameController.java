package controller;

import model.BoardModel;
import view.GameView;
import view.MainMenuView;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController {
    private MainMenuView mainMenuView;
    private MenuController menuController;
    private GameView gameView;
    private BoardModel boardModel;
    private boolean gameRunning;

    public GameController() {
        this.mainMenuView = new MainMenuView();
        this.menuController = new MenuController(this, mainMenuView);
        this.gameRunning = false;
    }

    public void start() {
        mainMenuView.setVisible(true);
    }

    public void startNewGame(int boardSize) {
        // Hide menu
        mainMenuView.setVisible(false);

        // Create new game
        boardModel = new BoardModel(boardSize);
        gameView = new GameView(boardModel);

        // Add keyboard listeners
        gameView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Set game as running
        gameRunning = true;

        // Show game view
        gameView.setVisible(true);
    }

    private void handleKeyPress(KeyEvent e) {
        // Check for Ctrl+Shift+Q
        if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_Q) {
            returnToMainMenu();
            return;
        }

        // Handle other key presses for game controls (to be implemented)
    }

    private void returnToMainMenu() {
        // Stop the game
        gameRunning = false;

        // Close game window
        if (gameView != null) {
            gameView.dispose();
        }

        // Show menu again
        mainMenuView.setVisible(true);
    }

    public void showHighScores() {
        // Will be implemented later
    }

    public void exit() {
        System.exit(0);
    }
}