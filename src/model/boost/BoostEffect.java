package model.boost;

import controller.GameController;

public abstract class BoostEffect {
    public abstract void apply(GameController controller);

    public void remove(GameController controller) {
        // Override in subclasses if cleanup is needed
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
