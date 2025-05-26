package model.threads;

import model.BoardModel;
import model.entity.Ghost;
import view.BoardView;
import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GhostThread extends Thread {
    private final Ghost ghost;
    private final BoardModel boardModel;
    private final BoardView boardView;
    private final GameController controller;
    private final int movementSpeed;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Random random = new Random();

    public GhostThread(Ghost ghost, BoardModel boardModel, BoardView boardView, int movementSpeed, GameController controller) {

        this.ghost = ghost;
        this.boardModel = boardModel;
        this.boardView = boardView;
        this.controller = controller;
        this.movementSpeed = movementSpeed;
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            doAction();
        }
    }

    private void doAction() {
        try {
            int row = ghost.getY();
            int col = ghost.getX();

            List<Integer> dirList = new ArrayList<>();

            if (ghost.isConfused()) {
                // Confused ghosts pick completely random directions (can repeat)
                for (int i = 0; i < 4; i++) {
                    dirList.add(random.nextInt(4)); // values 0–3
                }
            } else {
                dirList = new ArrayList<>(Arrays.asList(0, 1, 2, 3)); // 0: right, 1: down, 2: left, 3: up
                Collections.shuffle(dirList);

                int last = ghost.getLastDirection();
                int opposite = switch (last) {
                    case 0 -> 2;
                    case 1 -> 3;
                    case 2 -> 0;
                    case 3 -> 1;
                    default -> -1;
                };
                if (last != -1) dirList.remove((Integer) opposite);
            }

            boolean moved = false;

            for (int dir : dirList) {
                int newRow = row;
                int newCol = col;

                switch (dir) {
                    case 0 -> newCol = col + 1; // RIGHT
                    case 1 -> newRow = row + 1; // DOWN
                    case 2 -> newCol = col - 1; // LEFT
                    case 3 -> newRow = row - 1; // UP
                }

                if (boardModel.isValidPosition(newRow, newCol)
                        && !boardModel.isWall(newRow, newCol)
                        && !Objects.equals(boardModel.getValueAt(newRow, newCol), BoardModel.GHOST)) {

                    ghost.setX(newCol);
                    ghost.setY(newRow);
                    ghost.setLastDirection(dir);

                    boolean hitPacman = boardModel.moveGhost(row, col, newRow, newCol);
                    if (hitPacman && !controller.isInvincible()) {
                        controller.updateLives(-1);
                        controller.respawnAfterDeath();
                    }

                    moved = true;
                    break;
                }
            }

            if (!moved) {
                ghost.setLastDirection(-1);
            }

            SwingUtilities.invokeLater(boardView::repaint);
            Thread.sleep(movementSpeed);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void stopThread() {
        running = false;
        this.interrupt();
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        paused = false;
        synchronized (this) {
            notify();
        }
    }
}
