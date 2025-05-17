package model;

import model.utils.MazeGenerator;
import javax.swing.table.AbstractTableModel;

public class BoardModel extends AbstractTableModel {
    private Integer[][] board;
    private int size;

    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int DOT = 2;
    public static final int PACMAN = 3;
    public static final int GHOST = 4;
    public static final int POWERUP = 5;

    public BoardModel(int size) {
        this.size = size;
        this.board = new Integer[size][size];
        generateMaze();
    }

    private void generateMaze() {
        MazeGenerator mazeGenerator = new MazeGenerator(size, size);
        int[][] generatedMaze = mazeGenerator.generate();

        // Convert the int[][] maze to Integer[][] board
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
        return false; // Cells are not directly editable by the user
    }

    public boolean isWall(int row, int col) {
        return board[row][col] == WALL;
    }

    public boolean isDot(int row, int col) {
        return board[row][col] == DOT;
    }

    public boolean isPowerup(int row, int col) {
        return board[row][col] == POWERUP;
    }

    public int getSize() {
        return size;
    }

    public void movePacman(int fromRow, int fromCol, int toRow, int toCol) {
        // Check if the destination is valid (not a wall)
        if (toRow >= 0 && toRow < size && toCol >= 0 && toCol < size && !isWall(toRow, toCol)) {
            Integer currentCell = board[fromRow][fromCol];
            Integer targetCell = board[toRow][toCol];

            // Update the cells
            board[fromRow][fromCol] = EMPTY;
            board[toRow][toCol] = PACMAN;

            // Notify the view that these cells have changed
            fireTableCellUpdated(fromRow, fromCol);
            fireTableCellUpdated(toRow, toCol);
        }
    }

    public void moveGhost(int fromRow, int fromCol, int toRow, int toCol) {
        // Check if the destination is valid (not a wall)
        if (toRow >= 0 && toRow < size && toCol >= 0 && toCol < size && !isWall(toRow, toCol)) {
            Integer currentCell = board[fromRow][fromCol];
            Integer targetCell = board[toRow][toCol];

            // If the ghost is moving to an empty cell or a dot, remember the cell content
            Integer newFromCell = EMPTY;
            if (currentCell != GHOST) {
                // This happens if the ghost is on top of a dot or powerup
                newFromCell = currentCell;
            }

            // If the ghost is moving to a cell with Pacman, handle collision (to be implemented)

            // Update the cells
            board[fromRow][fromCol] = newFromCell;
            board[toRow][toCol] = GHOST;

            // Notify the view that these cells have changed
            fireTableCellUpdated(fromRow, fromCol);
            fireTableCellUpdated(toRow, toCol);
        }
    }

    // More methods will be added for game logic
}