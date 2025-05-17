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

    public Pacman(int x, int y, int speed) {
        super(x, y, speed);
        this.direction = 0;
        this.score = 0;
        this.lives = 3;
        this.poweredUp = false;
        this.animationFrame = 0;
        this.resourceManager = ResourceManager.getInstance();
    }

    @Override
    public void moveUp() {
        if (!isMoving) {
            direction = 3;
            isMoving = true;
        }
    }

    @Override
    public void moveDown() {
        if (!isMoving) {
            direction = 1;
            isMoving = true;
        }
    }

    @Override
    public void moveLeft() {
        if (!isMoving) {
            direction = 2;
            isMoving = true;
        }
    }

    @Override
    public void moveRight() {
        if (!isMoving) {
            direction = 0;
            isMoving = true;
        }
    }

    @Override
    public void updateAnimation() {
        // Update animation frame
        if (isMoving) {
            animationFrame = (animationFrame + 1) % 3;
        }
    }

    public Image getCurrentImage(int cellWidth, int cellHeight) {
        return resourceManager.getScaledPacmanImage(direction, animationFrame, cellWidth, cellHeight);
    }

    public int getDirection() {
        return direction;
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
}