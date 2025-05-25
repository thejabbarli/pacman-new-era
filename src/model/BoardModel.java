// ===========================
// Full Updated BoardModel.java
// ===========================

package model;

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
    public static final int POWERUP_SPEED = 6;
    public static final int POWERUP_INVULNERABLE = 7;
    public static final int POWERUP_EXTRALIFE = 8;
    public static final int POWERUP_FREEZE = 9;
    public static final int POWERUP_EAT = 10;

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
        if (row < 0 || row >= size || col < 0 || col >= size) return true;
        return board[row][col] == WALL;
    }

    public boolean isDot(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;
        return board[row][col] == DOT;
    }

    public boolean isPowerup(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;
        int val = board[row][col];
        return val == POWERUP || val == POWERUP_SPEED || val == POWERUP_INVULNERABLE ||
                val == POWERUP_EXTRALIFE || val == POWERUP_FREEZE || val == POWERUP_EAT;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public int getSize() {
        return size;
    }

    public void movePacman(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(toRow, toCol) || isWall(toRow, toCol)) return;

        if (board[fromRow][fromCol] == PACMAN) {
            board[fromRow][fromCol] = EMPTY;
            fireTableCellUpdated(fromRow, fromCol);
        }

        if (board[toRow][toCol] == DOT) {
            // Handle dot logic elsewhere
        }

        if (isPowerup(toRow, toCol)) {
            // Handle powerup logic elsewhere
        }

        board[toRow][toCol] = PACMAN;
        fireTableCellUpdated(toRow, toCol);
    }

    public void moveGhost(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValidPosition(toRow, toCol) || isWall(toRow, toCol)) return;

        Point from = new Point(fromCol, fromRow);
        Point to = new Point(toCol, toRow);

        Integer restore = ghostUnderTiles.getOrDefault(from, EMPTY);
        board[fromRow][fromCol] = restore;
        fireTableCellUpdated(fromRow, fromCol);

        ghostUnderTiles.put(to, board[toRow][toCol]);
        board[toRow][toCol] = GHOST;
        fireTableCellUpdated(toRow, toCol);

        ghostUnderTiles.remove(from);
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
