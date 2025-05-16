package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.BombermanGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Oneal extends Enemy {

    private int radius;
    private int direction; // 0: left, 1: right, 2: up, 3: down
    private boolean sprinted;

    private Random rand = new Random();

    public Oneal(int x, int y, Image img) {
        super(x, y, img);

        this.left_images.add(Sprite.oneal_left1.getFxImage());
        this.left_images.add(Sprite.oneal_left2.getFxImage());
        this.left_images.add(Sprite.oneal_left3.getFxImage());

        this.right_images.add(Sprite.oneal_right1.getFxImage());
        this.right_images.add(Sprite.oneal_right2.getFxImage());
        this.right_images.add(Sprite.oneal_right3.getFxImage());

        this.speed = 2;
        this.radius = 10 * Sprite.SCALED_SIZE;
        this.direction = rand.nextInt(4);
        this.sprinted = false;

    }

    @Override
    public void update() {
        move();
    }


    public void move() {

        //cap nhat toc do
        if (!sprinted && nearToBomberman()) {
            sprinted = true;
            speed *= 2;
        }

        if (sprinted && !nearToBomberman()) {
            sprinted = false;
            speed /= 2;
        }

        // cac huong kha di
        if (isCentered()) {
            chooseARandomDirection();
        }

        switch (direction) {
            case 0:
                setImg(getNextLeftImage());
                moveWithCollision(-1, 0);
//                moveToCell(-1, 0);
                break;
            case 1:
                setImg(getNextRightImage());
                moveWithCollision(1, 0);
//                moveToCell(1, 0);
                break;
            case 2:
                setImg(getNextLeftImage());
                moveWithCollision(0, -1);
//                moveToCell(0, -1);
                break;
            case 3:
                setImg(getNextRightImage());
                moveWithCollision(0, 1);
//                moveToCell(0, 1);
                break;
        }
    }

    public void moveToCell(int dx, int dy) {
        int prevX = getXFromRealX(realX);
        int prevY = getYFromRealY(realY);

        while (getXFromRealX(realX) == prevX && getYFromRealY(realY) == prevY) {
            moveWithCollision(dx, dy);
        }
    }

    public boolean nearToBomberman() {
        return distance() <= radius * radius;
    }


    public boolean isCentered() {
        int epsilon = 2;
        int cellCenterX = getXFromRealX(realX) * Sprite.SCALED_SIZE + Sprite.SCALED_SIZE / 2;
        int cellCenterY = getYFromRealY(realY) * Sprite.SCALED_SIZE + Sprite.SCALED_SIZE / 2;

        return Math.abs(realX + Sprite.SCALED_SIZE / 2 - cellCenterX) < epsilon
                && Math.abs(realY + Sprite.SCALED_SIZE / 2 - cellCenterY) < epsilon;
    }

    public void chooseARandomDirection() {
        int tileX = getXFromRealX(realX);
        int tileY = getYFromRealY(realY);
        List<Integer> possibleDirections = new ArrayList<>();
        if (BombermanGame.validate(tileX - 1, tileY)) possibleDirections.add(0);
        if (BombermanGame.validate(tileX + 1, tileY)) possibleDirections.add(1);
        if (BombermanGame.validate(tileX, tileY - 1)) possibleDirections.add(2);
        if (BombermanGame.validate(tileX, tileY + 1)) possibleDirections.add(3);

        direction = possibleDirections.get(rand.nextInt(possibleDirections.size()));
    }

    int distance() {
        return (int) (Math.pow(BombermanGame.getBomberman().getRealX() - realX, 2)
                 + Math.pow(BombermanGame.getBomberman().getRealY() - realY, 2));
    }
}
