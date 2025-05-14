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
        this.radius = 10;
        this.direction = rand.nextInt(4);
    }

    @Override
    public void update() {
        move();
    }

    public void move() {

        boolean found = false;
        if (distance() <= radius) {
            speed *= 2;
            found = true;
        }

        // Nếu đang ở giữa ô (center pixel)
        if (realX % Sprite.SCALED_SIZE == 0 && realY % Sprite.SCALED_SIZE == 0) {
            int tileX = getXFromRealX(realX);
            int tileY = getYFromRealY(realY);

            // Tìm hướng đi hợp lệ
            List<Integer> possibleDirections = new ArrayList<>();
            if (BombermanGame.validate(tileX - 1, tileY)) possibleDirections.add(0); // left
            if (BombermanGame.validate(tileX + 1, tileY)) possibleDirections.add(1); // right
            if (BombermanGame.validate(tileX, tileY - 1)) possibleDirections.add(2); // up
            if (BombermanGame.validate(tileX, tileY + 1)) possibleDirections.add(3); // down

            // Nếu hướng hiện tại không hợp lệ, hoặc có nhiều đường thì chọn lại
            if (!possibleDirections.contains(direction) || possibleDirections.size() > 2) {
                direction = possibleDirections.get(rand.nextInt(possibleDirections.size()));
            }
        }

        // Di chuyển theo hướng
        switch (direction) {
            case 0: // left
                setImg(getNextLeftImage());
                if (canMove(-speed, 0)) realX -= speed;
                break;
            case 1: // right
                setImg(getNextRightImage());
                if (canMove(speed, 0)) realX += speed;
                break;
            case 2: // up
                if (canMove(0, -speed)) realY -= speed;
                break;
            case 3: // down
                if (canMove(0, speed)) realY += speed;
                break;
        }

        if (found) {
            speed /= 2;
        }
    }

    private boolean canMove(int dx, int dy) {
        int newX = getXFromRealX(realX + dx);
        int newY = getYFromRealY(realY + dy);
        return BombermanGame.validate(newX, newY);
    }

    int distance() {
        return (int)Math.sqrt(Math.pow(BombermanGame.getBomberman().getX() - x, 2)
         + Math.pow(BombermanGame.getBomberman().getY() - y, 2));
    }
}
