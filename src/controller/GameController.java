package controller;

import model.BoardModel;
import model.entity.Pacman;
import model.entity.Ghost;
import model.threads.*;
import view.GameView;
import view.MainMenuView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    private MainMenuView mainMenuView;
    private MenuController menuController;
    private GameView gameView;
    private BoardModel boardModel;
    private boolean gameRunning;

    // Game entities
    private Pacman pacman;
    private List<Ghost> ghosts;

    // Threads
    private PacmanAnimationThread pacmanAnimationThread;
    private PacmanMovementThread pacmanMovementThread;
    private GameTimerThread gameTimerThread;
    private PowerUpGeneratorThread powerUpGeneratorThread;
    private List<GhostThread> ghostThreads;

    // Game stats
    private int score;
    private int lives;
    private int time;

    public GameController() {
        this.mainMenuView = new MainMenuView();
        this.menuController = new MenuController(this, mainMenuView);
        this.gameRunning = false;
        this.score = 0;
        this.lives = 3;
        this.time = 0;
        this.ghosts = new ArrayList<>();
        this.ghostThreads = new ArrayList<GhostThread>();
    }

    public void start() {
        mainMenuView.setVisible(true);
    }

    public void startNewGame(int boardSize) {
        // Hide menu
        mainMenuView.setVisible(false);

        // Reset game stats
        score = 0;
        lives = 3;
        time = 0;

        // Create new game board
        boardModel = new BoardModel(boardSize);

        // Create game entities
        createGameEntities();

        // Create game view
        gameView = new GameView(boardModel);

        // Initialize UI with initial values
        gameView.updateScore(score);
        gameView.updateLives(lives);
        gameView.updateTime(time);

        // Add keyboard listeners
        gameView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // Add window listener to return to main menu when game window is closed
        gameView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMainMenu();
            }
        });

        // Start game threads
        startGameThreads();

        // Set game as running
        gameRunning = true;

        // Show game view
        gameView.setVisible(true);
    }

    private void createGameEntities() {
        // Create Pacman
        int[] pacmanPos = boardModel.findPacmanPosition();
        if (pacmanPos != null) {
            pacman = new Pacman(pacmanPos[1], pacmanPos[0], 150); // x, y, speed
        } else {
            pacman = new Pacman(1, 1, 150); // Default fallback
        }

        ghosts.clear();

// Example: spawn 4 ghosts around center (adjust as needed)
        int boardSize = boardModel.getSize();
        int center = boardSize / 2;

        ghosts.add(new Ghost(center, center, 250, Color.RED));
        ghosts.add(new Ghost(center + 1, center, 250, Color.CYAN));
        ghosts.add(new Ghost(center, center + 1, 250, Color.ORANGE));
        ghosts.add(new Ghost(center - 1, center, 250, Color.PINK));

// Place ghosts on board
        for (Ghost ghost : ghosts) {
            boardModel.setValueAt(BoardModel.GHOST, ghost.getY(), ghost.getX());
        }

    }

    private void startGameThreads() {
        // Stop any existing threads
        stopGameThreads();

        // Create and start new threads
        pacmanAnimationThread = new PacmanAnimationThread(pacman, gameView.getBoardView(), 150);
        pacmanMovementThread = new PacmanMovementThread(pacman, boardModel, gameView.getBoardView(), 200);
        gameTimerThread = new GameTimerThread(this);
        powerUpGeneratorThread = new PowerUpGeneratorThread(boardModel, 5000, 25); // 5 seconds, 25% chance

        pacmanAnimationThread.start();
        pacmanMovementThread.start();
        gameTimerThread.start();
        powerUpGeneratorThread.start();

        for (Ghost ghost : ghosts) {
            GhostThread ghostThread = new GhostThread(ghost, boardModel, gameView.getBoardView(), 250);
            ghostThreads.add(ghostThread);
            ghostThread.start();
        }

    }

    private void stopGameThreads() {
        if (pacmanAnimationThread != null) {
            pacmanAnimationThread.stopThread();
            pacmanAnimationThread = null;
        }

        if (pacmanMovementThread != null) {
            pacmanMovementThread.stopThread();
            pacmanMovementThread = null;
        }

        if (gameTimerThread != null) {
            gameTimerThread.stopThread();
            gameTimerThread = null;
        }

        if (powerUpGeneratorThread != null) {
            powerUpGeneratorThread.stopThread();
            powerUpGeneratorThread = null;
        }

        // Stop all ghost threads
        for (GhostThread ghostThread : ghostThreads) {
            ghostThread.stopThread();
        }
        ghostThreads.clear();
    }

    private void handleKeyPress(KeyEvent e) {
        // Check for Ctrl+Shift+Q
        if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_Q) {
            returnToMainMenu();
            return;
        }

        // Handle movement keys
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                pacmanMovementThread.setDirection(PacmanMovementThread.UP);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                pacmanMovementThread.setDirection(PacmanMovementThread.DOWN);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                pacmanMovementThread.setDirection(PacmanMovementThread.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                pacmanMovementThread.setDirection(PacmanMovementThread.RIGHT);
                break;
            case KeyEvent.VK_P:
                togglePause();
                break;
        }
    }

    private void togglePause() {
        if (pacmanAnimationThread != null && pacmanMovementThread != null) {
            if (pacmanAnimationThread.isPaused()) {
                // Resume all threads
                resumeAllThreads();
            } else {
                // Pause all threads
                pauseAllThreads();
            }
        }
    }

    private void pauseAllThreads() {
        if (pacmanAnimationThread != null) pacmanAnimationThread.pauseThread();
        if (pacmanMovementThread != null) pacmanMovementThread.pauseThread();
        if (gameTimerThread != null) gameTimerThread.pauseThread();
        if (powerUpGeneratorThread != null) powerUpGeneratorThread.pauseThread();

        for (GhostThread ghostThread : ghostThreads) {
            ghostThread.pauseThread();
        }
    }

    private void resumeAllThreads() {
        if (pacmanAnimationThread != null) pacmanAnimationThread.resumeThread();
        if (pacmanMovementThread != null) pacmanMovementThread.resumeThread();
        if (gameTimerThread != null) gameTimerThread.resumeThread();
        if (powerUpGeneratorThread != null) powerUpGeneratorThread.resumeThread();

        for (GhostThread ghostThread : ghostThreads) {
            ghostThread.resumeThread();
        }
    }

    private void returnToMainMenu() {
        // Stop all game threads
        stopGameThreads();

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

    // Methods to update game stats
    public void updateScore(int points) {
        score += points;
        if (gameView != null) {
            gameView.updateScore(score);
        }
    }

    public void updateLives(int change) {
        lives += change;
        if (gameView != null) {
            gameView.updateLives(lives);
        }

        // Check for game over
        if (lives <= 0) {
            handleGameOver();
        }
    }

    public void updateTime(int seconds) {
        time = seconds;
        if (gameView != null) {
            gameView.updateTime(time);
        }
    }

    private void handleGameOver() {
        // Stop all threads
        stopGameThreads();

        // Show game over dialog
        JOptionPane.showMessageDialog(
                gameView,
                "Game Over!\nYour score: " + score,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Prompt for name and save high score
        String name = JOptionPane.showInputDialog(
                gameView,
                "Enter your name for the high score:",
                "High Score",
                JOptionPane.QUESTION_MESSAGE
        );

        if (name != null && !name.trim().isEmpty()) {
            // Save high score (to be implemented with Serializable)
            saveHighScore(name, score);
        }

        // Return to main menu
        returnToMainMenu();
    }

    private void saveHighScore(String name, int score) {
        // To be implemented later with Serializable
    }
}