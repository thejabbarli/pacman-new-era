package model.threads;

import controller.GameController;
import model.BoardModel;
import model.entity.Pacman;
import view.BoardView;

public class PacmanMovementThread extends GameThread {
    private final Pacman pacman;
    private final BoardModel boardModel;
    private final BoardView boardView;
    private final int movementSpeed; // milliseconds between movements

    // Directions
    public static final int RIGHT = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int UP = 3;

    private int nextDirection; // Direction pacman will move when possible
    private int currentDirection; // Direction pacman is currently moving
    private GameController controller;


    public PacmanMovementThread(Pacman pacman, BoardModel boardModel, BoardView boardView, int movementSpeed, GameController controller) {
        super();
        this.pacman = pacman;
        this.boardModel = boardModel;
        this.boardView = boardView;
        this.movementSpeed = movementSpeed;
        this.currentDirection = RIGHT;
        this.nextDirection = RIGHT;
        this.controller = controller; // ✅ FIX: Assign the controller here
    }


    public void setDirection(int direction) {
        this.nextDirection = direction;
    }

    @Override
    protected void doAction() {
        try {
            // Try to move in the requested direction first
            if (tryMove(nextDirection)) {
                currentDirection = nextDirection;
            }
            // Otherwise, try to continue in the current direction
            else if (!tryMove(currentDirection)) {
                // If can't move in any direction, just wait
                Thread.sleep(movementSpeed);
            }

            // Update the board view
            boardView.repaint();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean tryMove(int direction) throws InterruptedException {
        int row = pacman.getY();
        int col = pacman.getX();
        int newRow = row;
        int newCol = col;

        // Calculate new position based on direction
        switch (direction) {
            case RIGHT:
                newCol = col + 1;
                break;
            case DOWN:
                newRow = row + 1;
                break;
            case LEFT:
                newCol = col - 1;
                break;
            case UP:
                newRow = row - 1;
                break;
        }

        // Check if the new position is valid (not a wall)
        if (!boardModel.isWall(newRow, newCol)) {
            // Move Pacman in the model
            boardModel.movePacman(row, col, newRow, newCol);

            // Update Pacman's position
            pacman.setX(newCol);
            pacman.setY(newRow);
            pacman.setDirection(direction);

            // 🔥 Game logic checks
            controller.checkGhostCollision(newRow, newCol);
            controller.checkVictory();

            // Sleep for movement delay
            Thread.sleep(movementSpeed);

            return true;
        }


        return false;
    }
}