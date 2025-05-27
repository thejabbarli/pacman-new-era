package model.threads;

import model.entity.Ghost;
import view.BoardView;

public class GhostAnimationThread extends Thread {
    private final Ghost ghost;
    private final BoardView boardView;
    private final int frameDelay;
    private boolean running = true;

    public GhostAnimationThread(Ghost ghost, BoardView boardView, int frameDelay) {
        this.ghost = ghost;
        this.boardView = boardView;
        this.frameDelay = frameDelay;
    }

    @Override
    public void run() {
        while (running) {
            ghost.updateAnimation();
            boardView.repaint();

            try {
                Thread.sleep(frameDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopThread() {
        running = false;
        interrupt();
    }
}
