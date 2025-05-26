package model;

import controller.GameController;
import model.boost.BoostEffect;
import model.boost.BoostFactory;
import model.boost.HealthBoost;
import model.utils.MazeGenerator;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardModel extends AbstractTableModel {
    private Integer[][] board;
    private int size;

    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOT = 2;
    public static final int PACMAN = 3;
    public static final int GHOST = 4;
    public static final int POWERUP = 5;
    public static final int BOOST_HEALTH = 6;
    public static final int BOOST_THUNDER = 7;
    public static final int BOOST_ICE = 8;
    public static final int BOOST_POISON = 9;
    public static final int BOOST_SHIELD = 10;

    public static final int BIG_DOT = 11; // NEW: Big Dot (tileYemBig)
    private GameController controller;


    private final Map<Point, Integer> ghostUnderTiles = new HashMap<>();

    public BoardModel(int size) {
        this.size = size;
        this.board = new Integer[size][size];
        generateMaze();
    }

    private void generateMaze() {
        MazeGenerator mazeGenerator = new MazeGenerator(size, size);
        int[][] generatedMaze = mazeGenerator.generate();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = generatedMaze[i][j];
                if (board[i][j] == POWERUP) {
                    board[i][j] = BIG_DOT;
                }
            }
        }
    }

    @Override
    public int getRowCount() {
        return size;
    }

    @Override
    public int getColumnCount() {
        return size;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return board[rowIndex][columnIndex];
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (value instanceof Integer) {
            board[rowIndex][columnIndex] = (Integer) value;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public boolean isWall(int row, int col) {
        return !isValidPosition(row, col) || board[row][col] == WALL;
    }

    public boolean isDot(int row, int col) {
        if (!isValidPosition(row, col)) return false;
        int val = board[row][col];
        return val == DOT || val == BIG_DOT;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public int getSize() {
        return size;
    }
    public void setController(GameController controller) {
        this.controller = controller;
    }


    public int movePacman(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(toRow, toCol) || isWall(toRow, toCol)) return -1;

        int eaten = board[toRow][toCol];

        // Move Pacman
        board[fromRow][fromCol] = EMPTY;
        fireTableCellUpdated(fromRow, fromCol);

        board[toRow][toCol] = PACMAN;
        fireTableCellUpdated(toRow, toCol);

        // 🔁 Notify controller if it's a boost
        if (controller != null && eaten >= BOOST_HEALTH && eaten <= BOOST_SHIELD) {
            controller.onBoostCollected(eaten);
        }

        return eaten;
    }


    public boolean moveGhost(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(toRow, toCol) || isWall(toRow, toCol)) return false;

        Point from = new Point(fromCol, fromRow);
        Point to = new Point(toCol, toRow);

        boolean hitPacman = board[toRow][toCol] == PACMAN;

        // Save what's under the ghost before it moved out
        if (!ghostUnderTiles.containsKey(from)) {
            ghostUnderTiles.put(from, board[fromRow][fromCol] == GHOST ? DOT : board[fromRow][fromCol]);
        }

        int restore = ghostUnderTiles.getOrDefault(from, DOT);
        board[fromRow][fromCol] = restore;
        fireTableCellUpdated(fromRow, fromCol);
        ghostUnderTiles.remove(from);

        // Save what is currently at destination
        if (board[toRow][toCol] != GHOST) {
            ghostUnderTiles.put(to, board[toRow][toCol]);
        }

        // Place the ghost
        board[toRow][toCol] = GHOST;
        fireTableCellUpdated(toRow, toCol);

        return hitPacman;
    }

    public int[] findGhostPosition() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == GHOST) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public int[] findPacmanPosition() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == PACMAN) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }
}
