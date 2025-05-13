package uet.oop.bomberman.entities;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.HashSet;
import java.util.Set;

import static uet.oop.bomberman.BombermanGame.validate;

public class Bomber extends Entity {

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    public Bomber(int x, int y, Image img) {
        super(x, y, img);
        this.speed = 1;

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
        // Mỗi frame cập nhật lại vị trí thực để vẽ
        this.realX = this.x * Sprite.SCALED_SIZE;
        this.realY = this.y * Sprite.SCALED_SIZE;
    }

    public void handleKeyEvent(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (!pressedKeys.contains(code)) {
                pressedKeys.add(code);
                handleMovement(code);
            }
        });

        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
    }

    private void handleMovement(KeyCode code) {
        switch (code) {
            case W:
                if (BombermanGame.validate(x, y - speed)) {
                    y -= speed;
                    setImg(getNextUpImage());
                }
                break;
            case S:
                if (BombermanGame.validate(x, y + speed)) {
                    y += speed;
                    setImg(getNextDownImage());
                }
                break;
            case A:
                if (BombermanGame.validate(x - speed, y)) {
                    x -= speed;
                    setImg(getNextLeftImage());
                }
                break;
            case D:
                if (BombermanGame.validate(x + speed, y)) {
                    x += speed;
                    setImg(getNextRightImage());
                }
                break;
            case SPACE:
                // Sau này đặt bomb ở đây
                break;
        }
    }

    @Override
    public char getSymbol() {
        return 'p';
    }
}
