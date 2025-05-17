package model.threads;

import model.entity.Pacman;
import view.BoardView;

public class PacmanAnimationThread extends GameThread {
    private final Pacman pacman;
    private final BoardView boardView;
    private final int animationSpeed; // milliseconds between animation frames
    private int animationDirection = 1; // 1 for increasing, -1 for decreasing

    public PacmanAnimationThread(Pacman pacman, BoardView boardView, int animationSpeed) {
        super();
        this.pacman = pacman;
        this.boardView = boardView;
        this.animationSpeed = animationSpeed;
    }

    @Override
    protected void doAction() {
        try {
            // Update Pacman's animation frame
            int currentFrame = pacman.getAnimationFrame();

            // Calculate next frame using the 1-2-3-2-1 pattern
            if (currentFrame == 0) {
                animationDirection = 1; // going up
            } else if (currentFrame == 2) {
                animationDirection = -1; // going down
            }

            // Set the new frame
            pacman.setAnimationFrame(currentFrame + animationDirection);

            // Update the view
            boardView.repaint();

            // Sleep for animation delay
            Thread.sleep(animationSpeed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}