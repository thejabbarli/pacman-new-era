package view;

import model.BoardModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameView extends JFrame {
    private BoardView boardView;
    private BoardModel boardModel;

    public GameView(BoardModel boardModel) {
        this.boardModel = boardModel;

        setTitle("Pacman Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close just this window

        // Create board view
        boardView = new BoardView(boardModel);

        // Add board view to the frame
        add(boardView, BorderLayout.CENTER);

        // Add resize listener to handle window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardView.resizeBoard();
            }
        });

        // Add window listener to handle closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Return to main menu when this window is closed
                // This will be handled by the GameController
            }
        });

        // Set initial size based on board size
        int cellSize = 20; // Initial cell size
        int boardSize = boardModel.getSize();
        int frameWidth = boardSize * cellSize + 50; // Extra space for borders
        int frameHeight = boardSize * cellSize + 100; // Extra space for status panel and borders

        setSize(frameWidth, frameHeight);
        setLocationRelativeTo(null); // Center on screen
    }

    public void addKeyListener(KeyListener listener) {
        boardView.addKeyListener(listener);
    }

    public BoardView getBoardView() {
        return boardView;
    }

    public void updateScore(int score) {
        boardView.updateScore(score);
    }

    public void updateTime(int time) {
        boardView.updateTime(time);
    }

    public void updateLives(int lives) {
        boardView.updateLives(lives);
    }
}