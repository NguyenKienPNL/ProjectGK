package uet.oop.bomberman.entities;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.HashSet;
import java.util.Set;

public class Bomber extends Entity {
    private final int maxAnimate = 30;
    private final int animateDuration = 10;
    private int currentLevel;

    private int bombCount;
    private int bombRadius;
    private int flameBufftime = 0;
    private int speedBufftime = 0;
    private int bombBufftime = 0;
    private int animate = 0;
    private Bomb currentBomb;
    private boolean isDead = false;
    public static final int BUFF = 200;

    // THÊM DÒNG NÀY: Biến lưu trữ điểm số
    private int score;

    // Biến AudioClip để lưu trữ âm thanh bom nổ
    private AudioClip bombExplosionSound;
    // THÊM biến AudioClip để lưu trữ âm thanh tick khi đặt bom
    private AudioClip bombTickSound;

    private BombermanGame gameInstance;
    private boolean gameOverTriggered;

    public void setGameInstance(BombermanGame gameInstance) {
        this.gameInstance = gameInstance;
    }

    public Bomber(int x, int y, Image img) {
        super(x, y, img);
        this.speed = 8;
        this.bombRadius = 2;
        this.currentLevel = 1;
        this.bombCount = 1;
        this.score = 0; // THÊM DÒNG NÀY: Khởi tạo điểm số khi tạo Bomber
        this.gameOverTriggered = false;

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

        dead_images.add(Sprite.player_dead1.getFxImage());
        dead_images.add(Sprite.player_dead2.getFxImage());
        dead_images.add(Sprite.player_dead3.getFxImage());
    }

    // Getter và setter cho currentLevel
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    // THÊM DÒNG NÀY: Phương thức để lấy điểm số
    public int getScore() {
        return score;
    }

    // THÊM DÒNG NÀY: Phương thức để tăng điểm số
    public void addScore(int points) {
        this.score += points;
    }

    @Override
    public void update() {
//        this.realX = this.x * Sprite.SCALED_SIZE;
//        this.realY = this.y * Sprite.SCALED_SIZE;
        this.x = Math.round((float)this.realX / Sprite.SCALED_SIZE);
        this.y = Math.round((float)this.realY / Sprite.SCALED_SIZE);
        if (!isDead() && (BombermanGame.hasEnemyAt(realX, realY) || BombermanGame.hasFlameAt(x, y))) {
            System.out.println("Bomber va chạm với kẻ thù hoặc ngọn lửa! Bắt đầu chết.");
            destroy(); // Bắt đầu quá trình chết
        }

        if (!isDead()) {
            // Logic khi Bomber còn sống (giữ nguyên)
            if (BombermanGame.hasItemAt(x, y)) {
                Item item = BombermanGame.itemAt(x, y);
                if (item != null) {
                    item.applyEffect(this);
                    item.pickUp();
                }
            }

            if (speedBufftime > 0) {
                speedBufftime--;
                if (speedBufftime == 0) {
                    decreaseSpeed();
                }
            }

            if (flameBufftime > 0) {
                flameBufftime--;
                if (flameBufftime == 0) {
                    decreaseFlameLength();
                }
            }

            if (bombBufftime > 0) {
                bombBufftime--;
                if (bombBufftime == 0) {
                    decreaseBomb();
                }
            }
        } else {
            // Logic khi Bomber đã chết
            animate++;

            // Hiển thị animation chết trong một khoảng thời gian
            // animate / animateDuration sẽ cho chỉ số frame. Math.min để tránh IndexOutOfBounds.
            if (animate <= maxAnimate) {
                img = dead_images.get(Math.min(animate / animateDuration, dead_images.size() - 1));
            }

            // QUAN TRỌNG: Chuyển sang màn hình Game Over sau 1 giây (khoảng 60 frames ở 60 FPS)
            // và chỉ thực hiện MỘT LẦN duy nhất
            if (animate >= 60 && !gameOverTriggered) { // Sử dụng cờ gameOverTriggered để đảm bảo chỉ gọi một lần
                if (gameInstance != null && gameInstance.getMainAppInstance() != null) {
                    System.out.println("Bomber death animation finished. Showing Game Over screen."); // Debug message
                    // Gọi phương thức showGameResult của MainApp
                    gameInstance.getMainAppInstance().showGameResult(uet.oop.bomberman.UI.GameResult.LOSE);
                    gameOverTriggered = true; // Đặt cờ là true để không gọi lại
                    gameInstance.stopGame(); // Dừng vòng lặp game chính
                }
            }
        }
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
//                    BombermanGame.addEntity(new Flame(x, y, null, false, 0));
                    placeBomb();
                    break;
            }
        });
    }

    public boolean isDead() {
        return isDead;
    }

    public void destroy() {
        this.isDead = true;
        this.animate = 0;
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
        speed += 5;
    }

    public void decreaseSpeed() {
        speed -= 5;
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

    // CẬP NHẬT phương thức setter để nhận cả hai AudioClip
    public void setBombSounds(AudioClip bombTickSound, AudioClip explosionSound) {
        this.bombTickSound = bombTickSound; // LƯU ÂM THANH TICK
        this.bombExplosionSound = explosionSound;
    }

    // Trong lớp Bomber
    public void placeBomb() {
        // Kiểm tra xem vị trí hiện tại đã có bom chưa để tránh đặt chồng
        if (bombCount > 0 && BombermanGame.getEntitiesAt(x, y).stream().noneMatch(e -> e instanceof Bomb)) {
            // TRUYỀN CẢ bombExplosionSound VÀ bombTickSound VÀO CONSTRUCTOR CỦA BOMB
            Bomb bomb = new Bomb(x, y, this, bombExplosionSound, bombTickSound);
            currentBomb = bomb;
            BombermanGame.addEntity(bomb);
            bombCount--;
        }
    }

    // --- Giữ lại phương thức moveWithCollision và align ở đây để không sửa logic di chuyển cũ ---
    protected boolean moveWithCollision(int dx, int dy) {
        boolean moved = false;
        for (int i = 0; i < speed; i++) {
            // Kiểm tra vị trí pixel tiếp theo
            if (BombermanGame.validatePixelMove(realX + dx, realY + dy)) {
                realX += dx;
                realY += dy;
                moved = true;
            } else {
                // Dừng lại nếu gặp vật cản
                break;
            }
        }
        return moved;
    }

    protected void align(int dx, int dy) {
        if (dx != 0) { // đi ngang → căn trục dọc
            int remainder = realY % Sprite.SCALED_SIZE;
            if (remainder != 0) {
                if (remainder < Sprite.SCALED_SIZE / 2) {
                    realY -= remainder;
                } else {
                    realY += (Sprite.SCALED_SIZE - remainder);
                }
            }
        }

        if (dy != 0) { // đi dọc → căn trục ngang
            int remainder = realX % Sprite.SCALED_SIZE;
            if (remainder != 0) {
                if (remainder < Sprite.SCALED_SIZE / 2) {
                    realX -= remainder;
                } else {
                    realX += (Sprite.SCALED_SIZE - remainder);
                }
            }
        }
    }
    // --- Hết phần giữ lại ---
}