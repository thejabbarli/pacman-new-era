package model.entity;

import interfaces.Movement;

import java.awt.*;

public abstract class Entity implements Movement {
    protected int x;
    protected int y;
    protected Image[] sprites;
    protected int currentSpriteIndex;
    protected boolean isMoving;
    protected int speed;

    public Entity(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.isMoving = false;
        this.currentSpriteIndex = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }
    public abstract void updateAnimation();
    @Override
    public void stop() {
        isMoving = false;
    }
}