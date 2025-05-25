// ===========================
// Full Updated GameController.java
// ===========================

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

    private Pacman pacman;
    private List<Ghost> ghosts;

    private PacmanAnimationThread pacmanAnimationThread;
    private PacmanMovementThread pacmanMovementThread;
    private GameTimerThread gameTimerThread;
    private PowerUpGeneratorThread powerUpGeneratorThread;
    private List<GhostThread> ghostThreads;

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
        this.ghostThreads = new ArrayList<>();
    }

    public void start() {
        mainMenuView.setVisible(true);
    }

    public void startNewGame(int boardSize) {
        mainMenuView.setVisible(false);
        score = 0;
        lives = 3;
        time = 0;

        boardModel = new BoardModel(boardSize);
        createGameEntities();

        gameView = new GameView(boardModel);
        gameView.updateScore(score);
        gameView.updateLives(lives);
        gameView.updateTime(time);

        gameView.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        gameView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMainMenu();
            }
        });

        startGameThreads();
        gameRunning = true;
        gameView.setVisible(true);
    }

    private void createGameEntities() {
        int[] pacmanPos = boardModel.findPacmanPosition();
        if (pacmanPos != null) {
            pacman = new Pacman(pacmanPos[1], pacmanPos[0], 150);
        } else {
            pacman = new Pacman(1, 1, 150);
        }

        ghosts.clear();
        int boardSize = boardModel.getSize();
        int center = boardSize / 2;

        ghosts.add(new Ghost(center, center, 250, Color.RED));
        ghosts.add(new Ghost(center + 1, center, 250, Color.CYAN));
        ghosts.add(new Ghost(center, center + 1, 250, Color.ORANGE));
        ghosts.add(new Ghost(center - 1, center, 250, Color.PINK));

        for (Ghost ghost : ghosts) {
            boardModel.setValueAt(BoardModel.GHOST, ghost.getY(), ghost.getX());
        }
    }

    private void startGameThreads() {
        stopGameThreads();

        pacmanAnimationThread = new PacmanAnimationThread(pacman, gameView.getBoardView(), 150);
        pacmanMovementThread = new PacmanMovementThread(pacman, boardModel, gameView.getBoardView(), 200);
        gameTimerThread = new GameTimerThread(this);
        powerUpGeneratorThread = new PowerUpGeneratorThread(boardModel, 5000, 25);

        pacmanAnimationThread.start();
        pacmanMovementThread.start();
        gameTimerThread.start();
        powerUpGeneratorThread.start();

        for (Ghost ghost : ghosts) {
            GhostThread ghostThread = new GhostThread(ghost, boardModel, gameView.getBoardView(), 350);
            ghostThreads.add(ghostThread);
            ghostThread.start();
        }
    }

    private void stopGameThreads() {
        if (pacmanAnimationThread != null) pacmanAnimationThread.stopThread();
        if (pacmanMovementThread != null) pacmanMovementThread.stopThread();
        if (gameTimerThread != null) gameTimerThread.stopThread();
        if (powerUpGeneratorThread != null) powerUpGeneratorThread.stopThread();

        for (GhostThread ghostThread : ghostThreads) ghostThread.stopThread();
        ghostThreads.clear();
    }

    private void handleKeyPress(KeyEvent e) {
        if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_Q) {
            returnToMainMenu();
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> pacmanMovementThread.setDirection(PacmanMovementThread.UP);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> pacmanMovementThread.setDirection(PacmanMovementThread.DOWN);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> pacmanMovementThread.setDirection(PacmanMovementThread.LEFT);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> pacmanMovementThread.setDirection(PacmanMovementThread.RIGHT);
            case KeyEvent.VK_P -> togglePause();
        }
    }

    private void togglePause() {
        if (pacmanAnimationThread != null && pacmanMovementThread != null) {
            if (pacmanAnimationThread.isPaused()) {
                resumeAllThreads();
            } else {
                pauseAllThreads();
            }
        }
    }

    private void pauseAllThreads() {
        if (pacmanAnimationThread != null) pacmanAnimationThread.pauseThread();
        if (pacmanMovementThread != null) pacmanMovementThread.pauseThread();
        if (gameTimerThread != null) gameTimerThread.pauseThread();
        if (powerUpGeneratorThread != null) powerUpGeneratorThread.pauseThread();
        for (GhostThread ghostThread : ghostThreads) ghostThread.pauseThread();
    }

    private void resumeAllThreads() {
        if (pacmanAnimationThread != null) pacmanAnimationThread.resumeThread();
        if (pacmanMovementThread != null) pacmanMovementThread.resumeThread();
        if (gameTimerThread != null) gameTimerThread.resumeThread();
        if (powerUpGeneratorThread != null) powerUpGeneratorThread.resumeThread();
        for (GhostThread ghostThread : ghostThreads) ghostThread.resumeThread();
    }

    private void returnToMainMenu() {
        stopGameThreads();
        gameRunning = false;
        if (gameView != null) gameView.dispose();
        mainMenuView.setVisible(true);
    }

    public void showHighScores() {}

    public void exit() {
        System.exit(0);
    }

    public void updateScore(int points) {
        score += points;
        if (gameView != null) gameView.updateScore(score);
    }

    public void updateLives(int change) {
        lives += change;
        if (gameView != null) gameView.updateLives(lives);
        if (lives <= 0) handleGameOver();
    }

    public void updateTime(int seconds) {
        time = seconds;
        if (gameView != null) gameView.updateTime(time);
    }

    private void handleGameOver() {
        stopGameThreads();
        JOptionPane.showMessageDialog(gameView, "Game Over!\nYour score: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        String name = JOptionPane.showInputDialog(gameView, "Enter your name for the high score:", "High Score", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) saveHighScore(name, score);
        returnToMainMenu();
    }

    private void saveHighScore(String name, int score) {
        // Serializable implementation goes here
    }
}
