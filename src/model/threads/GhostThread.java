package model.threads;

import model.BoardModel;
import model.entity.Ghost;
import view.BoardView;

public class GhostThread extends GameThread {
    private final Ghost ghost;
    private final BoardModel boardModel;
    private final BoardView boardView;
    private final int movementSpeed;

    public GhostThread(Ghost ghost, BoardModel boardModel, BoardView boardView, int movementSpeed) {
        super();
        this.ghost = ghost;
        this.boardModel = boardModel;
        this.boardView = boardView;
        this.movementSpeed = movementSpeed;
    }

    @Override
    protected void doAction() {
        try {
            // Basic ghost movement logic (random for now)
            ghost.moveRandomly();

            // Try to move the ghost in the model
            int row = ghost.getY();
            int col = ghost.getX();
            int direction = ghost.getDirection();

            int newRow = row;
            int newCol = col;

            // Calculate new position based on direction
            switch (direction) {
                case 0: // RIGHT
                    newCol = col + 1;
                    break;
                case 1: // DOWN
                    newRow = row + 1;
                    break;
                case 2: // LEFT
                    newCol = col - 1;
                    break;
                case 3: // UP
                    newRow = row - 1;
                    break;
            }

            // Check if the new position is valid (not a wall)
            if (!boardModel.isWall(newRow, newCol)) {
                // Move ghost in the model
                boardModel.moveGhost(row, col, newRow, newCol);

                // Update ghost's position
                ghost.setX(newCol);
                ghost.setY(newRow);
            }

            // Update the board view
            boardView.repaint();

            // Sleep for movement delay
            Thread.sleep(movementSpeed);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}