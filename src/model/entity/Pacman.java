package model.entity;

import interfaces.Movement;
import model.utils.ResourceManager;

import java.awt.*;

public class Pacman extends Entity {
    private int direction;
    private int animationFrame;

    public Pacman(int x, int y, int speed) {
        super(x, y, speed);
        this.direction = 0;
        this.animationFrame = 0;
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
        if (isMoving()) {
            animationFrame = (animationFrame + 1) % 3;
        }
    }


    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getAnimationFrame() {
        return animationFrame;
    }
    public void setAnimationFrame(int frame) {

        this.animationFrame = Math.max(0, Math.min(2, frame));
    }

}