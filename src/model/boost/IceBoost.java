package model.boost;

import controller.GameController;

public class IceBoost extends BoostEffect {
    @Override
    public void apply(GameController controller) {
        controller.freezeGhosts(true);
    }

    @Override
    public void remove(GameController controller) {
        controller.freezeGhosts(false);
    }
}
