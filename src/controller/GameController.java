package controller;

import model.BoardModel;
import model.HighScoreManager;
import model.boost.BoostEffect;
import model.boost.BoostFactory;
import model.boost.HealthBoost;
import model.config.GameConstants;
import model.entity.GhostConfig;
import model.entity.Pacman;
import model.entity.Ghost;
import model.threads.*;
import model.utils.MazeGenerator;
import view.GameView;
import view.MainMenuView;
import view.HighScoresView;

import javax.swing.*;
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
    private final List<GhostAnimationThread> ghostAnimationThreads = new ArrayList<>();
    private int score;
    private int lives;
    private int time;
    private final HighScoreManager highScoreManager = new HighScoreManager();
    private boolean pacmanSpeedBoost = false;
    private boolean invincible = false;
    private boolean ghostsFrozen = false;
    private boolean ghostsConfused = false;

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
        MazeGenerator generator = new MazeGenerator(boardSize, boardSize);
        int[][] maze = generator.generate();
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                boardModel.setTile(r, c, maze[r][c]);
            }
        }
        boardModel.setController(this);
        clearGhostTiles();
        createGameEntities();
        gameView = new GameView(boardModel, ghosts);
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
        pacman = (pacmanPos != null) ? new Pacman(pacmanPos[1], pacmanPos[0], 150) : new Pacman(1, 1, 150);
        ghosts.clear();
        int boardSize = boardModel.getSize();
        int center = boardSize / 2;
        ghosts.clear();
        for (GhostConfig config : GameConstants.DEFAULT_GHOSTS) {
            int gx = center + config.offsetX;
            int gy = center + config.offsetY;
            Ghost ghost = new Ghost(gx, gy, config.speed, config.type);
            ghosts.add(ghost);
            boardModel.setTile(gy, gx, BoardModel.GHOST);
        }
        for (Ghost ghost : ghosts) {
            boardModel.setValueAt(BoardModel.GHOST, ghost.getY(), ghost.getX());
        }
    }

    private void startGameThreads() {
        stopGameThreads();
        pacmanAnimationThread = new PacmanAnimationThread(pacman, gameView.getBoardView(), 150);
        pacmanMovementThread = new PacmanMovementThread(pacman, boardModel, gameView.getBoardView(), 200, this);
        gameTimerThread = new GameTimerThread(this);
        powerUpGeneratorThread = new PowerUpGeneratorThread(boardModel, ghosts, 5000, 25);
        pacmanAnimationThread.start();
        pacmanMovementThread.start();
        gameTimerThread.start();
        powerUpGeneratorThread.start();
        ghostThreads.clear();
        ghostAnimationThreads.clear();
        for (Ghost ghost : ghosts) {
            GhostThread ghostThread = new GhostThread(ghost, boardModel, gameView.getBoardView(), 350, this);
            GhostAnimationThread animationThread = new GhostAnimationThread(ghost, gameView.getBoardView(), 300);
            ghostThreads.add(ghostThread);
            ghostAnimationThreads.add(animationThread);
            ghostThread.start();
            animationThread.start();
        }
    }

    private void clearGhostTiles() {
        for (int r = 0; r < boardModel.getSize(); r++) {
            for (int c = 0; c < boardModel.getSize(); c++) {
                if (boardModel.getTile(r, c) == BoardModel.GHOST) {
                    boardModel.setTile(r, c, BoardModel.DOT);
                }
            }
        }
    }

    private void stopGameThreads() {
        if (pacmanAnimationThread != null) pacmanAnimationThread.stopThread();
        if (pacmanMovementThread != null) pacmanMovementThread.stopThread();
        if (gameTimerThread != null) gameTimerThread.stopThread();
        if (powerUpGeneratorThread != null) powerUpGeneratorThread.stopThread();
        for (GhostThread ghostThread : ghostThreads) ghostThread.stopThread();
        ghostThreads.clear();
        for (GhostAnimationThread animationThread : ghostAnimationThreads) animationThread.stopThread();
        ghostAnimationThreads.clear();
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

    public void checkGhostCollision(int newRow, int newCol) {
        for (Ghost ghost : ghosts) {
            if (ghost.getX() == newCol && ghost.getY() == newRow) {
                updateLives(-1);
                respawnAfterDeath();
                break;
            }
        }
    }

    public void checkVictory() {
        for (int r = 0; r < boardModel.getSize(); r++) {
            for (int c = 0; c < boardModel.getSize(); c++) {
                if (boardModel.getValueAt(r, c).equals(BoardModel.DOT)) return;
            }
        }
        handleVictory();
    }

    public void respawnAfterDeath() {
        for (int r = 0; r < boardModel.getSize(); r++) {
            for (int c = 0; c < boardModel.getSize(); c++) {
                if (boardModel.getTile(r, c) == BoardModel.PACMAN) {
                    boardModel.setTile(r, c, BoardModel.EMPTY);
                }
            }
        }
        pacman.setX(1);
        pacman.setY(1);
        if (boardModel.getTile(1, 1) == BoardModel.GHOST) {
            boardModel.setTile(1, 1, BoardModel.DOT);
        }
        boardModel.setTile(1, 1, BoardModel.PACMAN);
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

    public void showHighScores() {
        new HighScoresView();
    }

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

    private void handleVictory() {
        stopGameThreads();
        JOptionPane.showMessageDialog(gameView, "You Win!\nYour score: " + score, "Victory", JOptionPane.INFORMATION_MESSAGE);
        String name = JOptionPane.showInputDialog(gameView, "Enter your name for the high score:", "High Score", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) saveHighScore(name.trim(), score);
        returnToMainMenu();
    }

    public boolean isPacmanSpeedBoost() {
        return pacmanSpeedBoost;
    }

    public void setPacmanSpeedBoost(boolean value) {
        this.pacmanSpeedBoost = value;
    }

    public void onBoostCollected(int boostType) {
        BoostEffect effect = BoostFactory.getBoostForType(boostType);
        if (effect == null) return;
        if (effect instanceof HealthBoost) {
            effect.apply(this);
        } else {
            effect.applyWithDuration(this, 5000);
        }
    }

    public void setInvincible(boolean value) {
        this.invincible = value;
    }

    public void freezeGhosts(boolean value) {
        this.ghostsFrozen = value;
        for (GhostThread thread : ghostThreads) {
            if (value) {
                thread.pauseThread();
            } else {
                thread.resumeThread();
            }
        }
    }

    public void setGhostsConfused(boolean value) {
        this.ghostsConfused = value;
        for (Ghost ghost : ghosts) {
            ghost.setConfused(value);
        }
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void saveHighScore(String name, int score) {
        highScoreManager.addScore(name, score);
    }
}
