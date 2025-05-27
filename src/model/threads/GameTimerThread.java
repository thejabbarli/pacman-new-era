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
            Thread.sleep(1000);
            seconds++;
            gameController.updateTime(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}