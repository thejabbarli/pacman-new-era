package model.entity;

public class GhostConfig {
    public final int offsetX;
    public final int offsetY;
    public final int speed;
    public final String type; // e.g., "blinky", "pinky", etc.

    public GhostConfig(int offsetX, int offsetY, int speed, String type) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.speed = speed;
        this.type = type;
    }
}
