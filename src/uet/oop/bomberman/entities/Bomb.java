package uet.oop.bomberman.entities;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;
import javafx.scene.media.AudioClip; // THÊM import này

public class Bomb extends Entity {
    private static final int TIME_TO_EXPLORE = 120;
    private int countdown = TIME_TO_EXPLORE;
    private Bomber owner;
    private boolean exploded = false;
    private FlameSegments flameSegments;
    private List<Image> bombAnimation = new ArrayList<>();
    private AudioClip explosionSound; // THÊM biến này để lưu trữ âm thanh "bùm"

    // Cập nhật constructor để nhận AudioClip cho tiếng nổ
    public Bomb(int x, int y, Bomber owner, AudioClip explosionSound) { // SỬA DÒNG NÀY
        super(x, y, Sprite.bomb.getFxImage());
        this.owner = owner;
        this.explosionSound = explosionSound; // Gán AudioClip được truyền vào
        initAnimation();
    }

    private void initAnimation() {
        bombAnimation.add(Sprite.bomb.getFxImage());
        bombAnimation.add(Sprite.bomb_1.getFxImage());
        bombAnimation.add(Sprite.bomb_2.getFxImage());
    }

    @Override
    public void update() {
        if(!exploded) {
            countdown--;
            animate();
            if (countdown <= 0) {
                exploded();
            }
        } else if (flameSegments != null) {
            flameSegments.update();
            if(flameSegments.isFinished()) {
                // Dòng này cần xem xét lại logic của bạn.
                // Nếu mục đích là xóa FlameSegments, bạn có thể cần một phương thức
                // removeEntity tổng quát hơn trong BombermanGame, ví dụ:
                // BombermanGame.removeEntity(flameSegments);
                // Giữ nguyên logic hiện tại của bạn nếu bạn đã có cách xử lý này.
                BombermanGame.removeFlame(this);
            }
        }
    }

    private void animate() {
        int frame = (TIME_TO_EXPLORE - countdown) / 20;
        if(frame < bombAnimation.size()) {
            img = bombAnimation.get(frame);
        }
    }

    public void exploded() {
        exploded = true;
        img = Sprite.bomb_exploded.getFxImage();

        // PHÁT ÂM THANH "BÙM" KHI BOM NỔ
        if (explosionSound != null) {
            explosionSound.play();
        }

        flameSegments = new FlameSegments(x, y, owner );
        owner.decreaseBomb();
        BombermanGame.addEntity(flameSegments);
    }
    public boolean isExploded() {
        return exploded;
    }

    public Bomber getOwner() {
        return owner;
    }
}