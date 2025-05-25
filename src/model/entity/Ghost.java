package model.entity;

import interfaces.Movement;
import java.awt.*;
import java.util.Random;

public class Ghost extends Entity {
    private int direction; // 0: right, 1: down, 2: left, 3: up
    private Color color;
    private Random random;
    private boolean isVulnerable;
    private int lastDirection = -1;


    public Ghost(int x, int y, int speed, Color color) {
        super(x, y, speed);
        this.direction = 0;
        this.color = color;
        this.random = new Random();
        this.isVulnerable = false;

        // We'll load the sprites in a more sophisticated way later
        // For now, this is a placeholder
        this.sprites = new Image[4]; // 1 frame for each direction
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

    public void moveRandomly() {
        int newDirection = random.nextInt(4);
        switch (newDirection) {
            case 0:
                moveRight();
                break;
            case 1:
                moveDown();
                break;
            case 2:
                moveLeft();
                break;
            case 3:
                moveUp();
                break;
        }
    }

    @Override
    public void updateAnimation() {
        // Simple animation: use the sprite corresponding to the direction
        currentSpriteIndex = direction;
    }

    public int getDirection() {
        return direction;
    }

    public Color getColor() {
        return color;
    }

    public boolean isVulnerable() {
        return isVulnerable;
    }

    public void setVulnerable(boolean vulnerable) {
        isVulnerable = vulnerable;
    }

    public int getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(int dir) {
        this.lastDirection = dir;
    }
}