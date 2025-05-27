package model.entity;

import interfaces.Movement;
import java.awt.*;
import java.util.Random;

public class Ghost extends Entity {
    private int direction;
    private final Random random;

    private int lastDirection = -1;
    private boolean confused = false;
    private final String type;
    private int animationFrame = 1;


    public Ghost(int x, int y, int speed, String type) {
        super(x, y, speed);
        this.direction = 0;
        this.random = new Random();
        this.type = type;
        this.sprites = new Image[2];
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
            case 0 -> moveRight();
            case 1 -> moveDown();
            case 2 -> moveLeft();
            case 3 -> moveUp();
        }
    }
    public String getType() {
        return type;
    }

    @Override
    public void updateAnimation() {
        // Toggle between 1 and 2
        animationFrame = (animationFrame == 1) ? 2 : 1;
    }
    public int getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(int dir) {
        this.lastDirection = dir;
    }

    public void setConfused(boolean confused) {
        this.confused = confused;
    }

    public boolean isConfused() {
        return confused;
    }

    public int getAnimationFrame() {
        return animationFrame;
    }

}
