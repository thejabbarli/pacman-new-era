package model.boost;

import controller.GameController;

public class ShieldBoost extends BoostEffect {
    @Override
    public void apply(GameController controller) {
        controller.setInvincible(true);
    }

    @Override
    public void remove(GameController controller) {
        controller.setInvincible(false);
    }
}
