package model.boost;

import controller.GameController;

public class PoisonBoost extends BoostEffect {
    @Override
    public void apply(GameController controller) {
        controller.setGhostsConfused(true);
    }

    @Override
    public void remove(GameController controller) {
        controller.setGhostsConfused(false);
    }
}
