package model.threads;

import controller.GameController;
import model.BoardModel;
import model.entity.Pacman;
import view.BoardView;

public class PacmanMovementThread extends GameThread {
    private final Pacman pacman;
    private final BoardModel boardModel;
    private final BoardView boardView;
    private final int movementSpeed;

    public static final int RIGHT = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int UP = 3;

    private int nextDirection;
    private int currentDirection;
    private final GameController controller;

    private final int normalDelay = 140;
    private final int boostedDelay = 40;

    public PacmanMovementThread(Pacman pacman, BoardModel boardModel, BoardView boardView, int movementSpeed, GameController controller) {
        super();
        this.pacman = pacman;
        this.boardModel = boardModel;
        this.boardView = boardView;
        this.movementSpeed = movementSpeed;
        this.controller = controller;
        this.currentDirection = RIGHT;
        this.nextDirection = RIGHT;
    }

    public void setDirection(int direction) {
        this.nextDirection = direction;
    }

    @Override
    protected void doAction() {
        try {
            if (tryMove(nextDirection)) {
                currentDirection = nextDirection;
            } else if (!tryMove(currentDirection)) {
                Thread.sleep(movementSpeed);
            }

            boardView.repaint();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean tryMove(int direction) throws InterruptedException {
        for (int r = 0; r < boardModel.getSize(); r++) {
            for (int c = 0; c < boardModel.getSize(); c++) {
                if (boardModel.getValueAt(r, c).equals(BoardModel.PACMAN)
                        && (r != pacman.getY() || c != pacman.getX())) {
                    boardModel.setValueAt(BoardModel.EMPTY, r, c);
                }
            }
        }

        int row = pacman.getY();
        int col = pacman.getX();
        int newRow = row;
        int newCol = col;

        switch (direction) {
            case RIGHT -> newCol++;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case UP -> newRow--;
        }

        if (!boardModel.isValidPosition(newRow, newCol) || boardModel.isWall(newRow, newCol)) return false;

        boardModel.setValueAt(BoardModel.EMPTY, row, col);

        int eaten = boardModel.movePacman(row, col, newRow, newCol);
        pacman.setX(newCol);
        pacman.setY(newRow);
        pacman.setDirection(direction);

        switch (eaten) {
            case BoardModel.DOT -> controller.updateScore(10);
            case BoardModel.BIG_DOT -> controller.updateScore(50);
            case BoardModel.BOOST_HEALTH,
                    BoardModel.BOOST_SHIELD,
                    BoardModel.BOOST_THUNDER,
                    BoardModel.BOOST_ICE,
                    BoardModel.BOOST_POISON -> controller.onBoostCollected(eaten);
        }

        controller.checkGhostCollision(newRow, newCol);
        controller.checkVictory();

        int delay = controller.isPacmanSpeedBoost() ? boostedDelay : normalDelay;
        Thread.sleep(delay);

        Thread.sleep(movementSpeed);
        return true;
    }
}
