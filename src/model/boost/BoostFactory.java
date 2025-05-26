package model.boost;

import model.BoardModel;

public class BoostFactory {
    public static BoostEffect getBoostForType(int boostType) {
        return switch (boostType) {
            case BoardModel.BOOST_HEALTH -> new HealthBoost();
            case BoardModel.BOOST_SHIELD -> new ShieldBoost();
            case BoardModel.BOOST_THUNDER -> new ThunderBoost();
            case BoardModel.BOOST_ICE -> new IceBoost();
            case BoardModel.BOOST_POISON -> new PoisonBoost();
            default -> null;
        };
    }
}
