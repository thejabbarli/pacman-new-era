package model.threads;

import model.entity.Pacman;
import view.BoardView;

public class PacmanAnimationThread extends GameThread {
    private final Pacman pacman;
    private final BoardView boardView;
    private final int animationSpeed;
    private int animationDirection = 1;

    public PacmanAnimationThread(Pacman pacman, BoardView boardView, int animationSpeed) {
        super();
        this.pacman = pacman;
        this.boardView = boardView;
        this.animationSpeed = animationSpeed;
    }

    protected void doAction() {
        try {
            int currentFrame = pacman.getAnimationFrame();
            if (currentFrame == 0) animationDirection = 1;
            else if (currentFrame == 2) animationDirection = -1;

            pacman.setAnimationFrame(currentFrame + animationDirection);
            boardView.updatePacmanRenderState(pacman.getDirection(), pacman.getAnimationFrame());

            boardView.repaint();
            Thread.sleep(animationSpeed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}