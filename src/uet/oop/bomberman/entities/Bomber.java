package uet.oop.bomberman.entities;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;
import javafx.scene.media.AudioClip; // THÊM import này

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static uet.oop.bomberman.BombermanGame.validatePixelMove;

public class Bomber extends Entity { // Đã sửa từ Entity sang Character (nếu Character là lớp cha đúng)


    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private int currentLevel;

    private int bombCount;
    private int bombRadius;
    private int flameBufftime = 0;
    private int speedBufftime = 0;
    private int bombBufftime = 0;
    public static final int BUFF = 200;
//    thoi gian duoc nhan buff

    // THÊM hai biến AudioClip này
    private AudioClip bombPlaceSound;     // Âm thanh "tít"
    private AudioClip bombExplosionSound; // Âm thanh "bùm"


    public Bomber(int x, int y, Image img) {
        super(x, y, img);
        this.speed = 8;
        this.bombRadius = 4;
        this.currentLevel = 1;
        this.bombCount = 1;

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

    // Getter và setter cho currentLevel
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    // THÊM phương thức setter này để BombermanGame truyền âm thanh vào
    public void setBombSounds(AudioClip placeSound, AudioClip explosionSound) {
        this.bombPlaceSound = placeSound;
        this.bombExplosionSound = explosionSound;
    }

    @Override
    public void update() {
//        this.realX = this.x * Sprite.SCALED_SIZE;
//        this.realY = this.y * Sprite.SCALED_SIZE;
        this.x = Math.round((float)this.realX / Sprite.SCALED_SIZE);
        this.y = Math.round((float)this.realY / Sprite.SCALED_SIZE);
//        het time buff tro ve trang thai ban dau
        if(speedBufftime > 0) {
            speedBufftime--;
            if(speedBufftime == 0) {
                decreaseSpeed();
            }
        }

        if(flameBufftime > 0) {
            flameBufftime--;
            if(flameBufftime == 0) {
                decreaseFlameLength();
            }
        }

        if(bombBufftime > 0) {
            bombBufftime--;
            if(bombBufftime == 0) {
                decreaseBomb();
            }
        }
    }

    //@Override
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
                    placeBomb(); // Gọi phương thức đặt bom
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

    public void setBombBufftime(int bomb) {
        this.bombBufftime = bomb;
    }

    public void setFlameBufftime(int flame) {
        this.flameBufftime = flame;
    }

    public void setSpeedBufftime(int speed) {
        this.speedBufftime = speed;
    }

    // Phương thức đặt bom
    public void placeBomb() {
        if (bombCount > 0) {
            int bombX = (int) Math.round((realX + Sprite.SCALED_SIZE / 2.0) / Sprite.SCALED_SIZE);
            int bombY = (int) Math.round((realY + Sprite.SCALED_SIZE / 2.0) / Sprite.SCALED_SIZE);

            // Kiểm tra xem đã có bom tại vị trí này chưa
            boolean hasBombAtPos = false;
            for (Entity entity : BombermanGame.getEntitiesAt(bombX, bombY)) {
                if (entity instanceof Bomb) {
                    hasBombAtPos = true;
                    break;
                }
            }

            if (!hasBombAtPos) {
                // Tạo Bomb và truyền AudioClip cho tiếng nổ "bùm"
                Bomb bomb = new Bomb(bombX, bombY, this, bombExplosionSound); // SỬA ĐỂ TRUYỀN explosionSound
                BombermanGame.addEntity(bomb);
                bombCount--;

                // Phát âm thanh "tít" khi đặt bom
                if (bombPlaceSound != null) {
                    bombPlaceSound.play();
                }
            }
        }
    }

    // Thêm Override vì đây là phương thức từ lớp cha
    public double getSpeed() { // Đã sửa kiểu trả về từ int sang double cho phù hợp với khai báo
        return speed;
    }
}