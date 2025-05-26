package model.boost;

import controller.GameController;

public class HealthBoost extends BoostEffect {
    @Override
    public void apply(GameController controller) {
        controller.updateLives(1);
    }
}
