package view;

import model.BoardModel;
import model.entity.Ghost;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class GameView extends JFrame {
    private final BoardView boardView;

    public GameView(BoardModel boardModel, List<Ghost> ghosts) {

        setTitle("Pacman Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        boardView = new BoardView(boardModel, ghosts);
        add(boardView, BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boardView.resizeBoard();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });

        int cellSize = 20;
        int boardSize = boardModel.getSize();
        int frameWidth = boardSize * cellSize + 50;
        int frameHeight = boardSize * cellSize + 100;
        setSize(frameWidth, frameHeight);
        setLocationRelativeTo(null);
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
