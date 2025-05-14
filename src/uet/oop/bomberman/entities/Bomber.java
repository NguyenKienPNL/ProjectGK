package uet.oop.bomberman.entities;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static uet.oop.bomberman.BombermanGame.validatePixelMove;

public class Bomber extends Entity {

    private int bombCount;
    private int bombRadius;

    public Bomber(int x, int y, Image img) {
        super(x, y, img);
        this.speed = 8;

        // Load sprite animations
        left_images.add(Sprite.player_left.getFxImage());
        left_images.add(Sprite.player_left_1.getFxImage());
        left_images.add(Sprite.player_left_2.getFxImage());

        right_images.add(Sprite.player_right.getFxImage());
        right_images.add(Sprite.player_right_1.getFxImage());
        right_images.add(Sprite.player_right_2.getFxImage());

        up_images.add(Sprite.player_up.getFxImage());
        up_images.add(Sprite.player_up_1.getFxImage());
        up_images.add(Sprite.player_up_2.getFxImage());

        down_images.add(Sprite.player_down.getFxImage());
        down_images.add(Sprite.player_down_1.getFxImage());
        down_images.add(Sprite.player_down_2.getFxImage());
    }

    @Override
    public void update() {
//        this.realX = this.x * Sprite.SCALED_SIZE;
//        this.realY = this.y * Sprite.SCALED_SIZE;
        this.x = Math.round((float)this.realX / Sprite.SCALED_SIZE);
        this.y = Math.round((float)this.realY / Sprite.SCALED_SIZE);
    }

    public void handleKeyEvent(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    setImg(getNextUpImage());
                    align(0, -1);
                    moveWithCollision(0, -1);
                    break;
                case S:
                    setImg(getNextDownImage());
                    align(0, 1);
                    moveWithCollision(0, 1);
                    break;
                case A:
                    setImg(getNextLeftImage());
                    align(-1, 0);
                    moveWithCollision(-1, 0);
                    break;
                case D:
                    setImg(getNextRightImage());
                    align(1, 0);
                    moveWithCollision(1, 0);
                    break;
                case SPACE:
                    // sau này đặt bomb
                    break;
            }
        });
    }

    public int getBombCount() {
        return bombCount;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    public int getBombRadius() {
        return bombRadius;
    }

    public void setBombRadius(int bombRadius) {
        this.bombRadius = bombRadius;
    }

    public void increaseBomb() {
        bombCount++;
    }

    public void decreaseBomb() {
        bombCount--;
    }

    public void increaseFlameLength() {
        bombRadius++;
    }

    public void decreaseFlameLength() {
        bombRadius--;
    }

    public void increaseSpeed() {
        speed++;
    }

    public void decreaseSpeed() {
        speed--;
    }
}
