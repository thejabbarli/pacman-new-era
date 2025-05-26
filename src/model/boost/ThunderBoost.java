package model.boost;

import controller.GameController;

public class ThunderBoost extends BoostEffect {
    @Override
    public void apply(GameController controller) {
        controller.setPacmanSpeedBoost(true);
    }

    @Override
    public void remove(GameController controller) {
        controller.setPacmanSpeedBoost(false);
    }
}
