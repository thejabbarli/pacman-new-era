package model.boost;

import controller.GameController;

public abstract class BoostEffect {
    public abstract void apply(GameController controller);

    public void remove(GameController controller) {

    }

    public void applyWithDuration(GameController controller, int durationMillis) {
        apply(controller);
        new Thread(() -> {
            try {
                Thread.sleep(durationMillis);
                remove(controller);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }
}
