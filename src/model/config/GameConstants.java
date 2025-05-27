package model.config;

import model.entity.GhostConfig;

import java.util.List;

public class GameConstants {
    public static final List<GhostConfig> DEFAULT_GHOSTS = List.of(

            new GhostConfig(0, 0, 250, "blinky"),
            new GhostConfig(1, 0, 250, "inky"),
            new GhostConfig(0, 1, 250, "pinky"),
            new GhostConfig(-1, 0, 250, "clyde")


    );
}
