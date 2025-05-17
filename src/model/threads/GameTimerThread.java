package model.threads;

import controller.GameController;

public class GameTimerThread extends GameThread {
    private final GameController gameController;
    private int seconds;

    public GameTimerThread(GameController gameController) {
        super();
        this.gameController = gameController;
        this.seconds = 0;
    }

    @Override
    protected void doAction() {
        try {
            // Sleep for 1 second
            Thread.sleep(1000);

            // Increment the time
            seconds++;

            // Update the view with new time
            gameController.updateTime(seconds);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public void resetTimer() {
        this.seconds = 0;
    }
}