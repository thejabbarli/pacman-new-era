package model.entity;

import interfaces.Movement;
import model.utils.ResourceManager;

import java.awt.*;

public class Pacman extends Entity {
    private int direction; // 0: right, 1: down, 2: left, 3: up
    private int score;
    private int lives;
    private boolean poweredUp;
    private int animationFrame;
    private ResourceManager resourceManager;

    // Powerup effects
    private boolean speedBoost;
    private boolean invulnerable;
    private boolean freezeGhosts;
    private int powerUpTimer;

    public Pacman(int x, int y, int speed) {
        super(x, y, speed);
        this.direction = 0;
        this.score = 0;
        this.lives = 3;
        this.poweredUp = false;
        this.animationFrame = 0;
        this.resourceManager = ResourceManager.getInstance();

        this.speedBoost = false;
        this.invulnerable = false;
        this.freezeGhosts = false;
        this.powerUpTimer = 0;
    }

    @Override
    public void moveUp() {
        direction = 3;
        setMoving(true);
    }

    @Override
    public void moveDown() {
        direction = 1;
        setMoving(true);
    }

    @Override
    public void moveLeft() {
        direction = 2;
        setMoving(true);
    }

    @Override
    public void moveRight() {
        direction = 0;
        setMoving(true);
    }

    @Override
    public void updateAnimation() {
        // Update animation frame if moving
        if (isMoving()) {
            animationFrame = (animationFrame + 1) % 3;
        }
    }

    public Image getCurrentImage(int cellWidth, int cellHeight) {
        return resourceManager.getScaledPacmanImage(direction, animationFrame, cellWidth, cellHeight);
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
    }

    public void gainLife() {
        lives++;
    }

    public boolean isPoweredUp() {
        return poweredUp;
    }

    public void setPoweredUp(boolean poweredUp) {
        this.poweredUp = poweredUp;
    }

    public int getAnimationFrame() {
        return animationFrame;
    }

    // Powerup methods
    public void activateSpeedBoost() {
        speedBoost = true;
        powerUpTimer = 10; // 10 seconds
    }

    public void activateInvulnerability() {
        invulnerable = true;
        poweredUp = true;
        powerUpTimer = 10; // 10 seconds
    }

    public void activateFreezeGhosts() {
        freezeGhosts = true;
        powerUpTimer = 5; // 5 seconds
    }

    public void updatePowerUpEffects() {
        if (powerUpTimer > 0) {
            powerUpTimer--;
            if (powerUpTimer == 0) {
                // Deactivate all powerups
                speedBoost = false;
                invulnerable = false;
                freezeGhosts = false;
                poweredUp = false;
            }
        }
    }
    // Add this method to the Pacman class
    public void setAnimationFrame(int frame) {
        // Ensure the frame is within bounds (0-2)
        this.animationFrame = Math.max(0, Math.min(2, frame));
    }

    public boolean hasSpeedBoost() {
        return speedBoost;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public boolean canFreezeGhosts() {
        return freezeGhosts;
    }

    public int getPowerUpTimer() {
        return powerUpTimer;
    }
}