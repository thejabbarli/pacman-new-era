package model;

import controller.GameController;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardModel extends AbstractTableModel {
    private final int size;
    private final Integer[][] board;

    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOT = 2;
    public static final int PACMAN = 3;
    public static final int GHOST = 4;
    public static final int BIG_DOT = 5;
    public static final int BOOST_HEALTH = 6;
    public static final int BOOST_THUNDER = 7;
    public static final int BOOST_ICE = 8;
    public static final int BOOST_POISON = 9;
    public static final int BOOST_SHIELD = 10;

    private GameController controller;
    private final Map<Point, Integer> ghostUnderTiles = new HashMap<>();

    public BoardModel(int size) {
        this.size = size;
        this.board = new Integer[size][size];
        initializeEmptyBoard();
    }

    private void initializeEmptyBoard() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = EMPTY;
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
    public Object getValueAt(int row, int col) {
        return board[row][col];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (value instanceof Integer) {
            board[row][col] = (Integer) value;
            fireTableCellUpdated(row, col);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setTile(int row, int col, int value) {
        if (isValid(row, col)) {
            board[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    public int getTile(int row, int col) {
        if (!isValid(row, col)) return WALL;
        Integer val = board[row][col];
        return (val == null) ? EMPTY : val;
    }

    public boolean isWall(int row, int col) {
        return getTile(row, col) == WALL;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }

    public int getSize() {
        return size;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public int[] findTile(int tileType) {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (board[r][c] == tileType)
                    return new int[]{r, c};
        return null;
    }

    public int[] findPacmanPosition() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] == PACMAN)
                    return new int[]{r, c};
            }
        }
        return null;
    }

    public int movePacman(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValid(toRow, toCol) || isWall(toRow, toCol)) return -1;
        int eaten = board[toRow][toCol];
        setTile(fromRow, fromCol, EMPTY);
        setTile(toRow, toCol, PACMAN);
        return eaten;
    }

    public boolean moveGhost(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isValid(toRow, toCol) || isWall(toRow, toCol)) return false;
        Point from = new Point(fromCol, fromRow);
        Point to = new Point(toCol, toRow);
        boolean hitPacman = getTile(toRow, toCol) == PACMAN;
        if (!ghostUnderTiles.containsKey(from)) {
            ghostUnderTiles.put(from, DOT);
        }
        board[fromRow][fromCol] = ghostUnderTiles.remove(from);
        if (board[fromRow][fromCol] == null) board[fromRow][fromCol] = DOT;
        fireTableCellUpdated(fromRow, fromCol);
        Integer under = board[toRow][toCol];
        ghostUnderTiles.put(to, (under != null) ? under : DOT);
        board[toRow][toCol] = GHOST;
        fireTableCellUpdated(toRow, toCol);
        return hitPacman;
    }
}
