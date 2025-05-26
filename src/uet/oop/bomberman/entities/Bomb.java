package uet.oop.bomberman.entities;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;
import javafx.scene.media.AudioClip;

public class Bomb extends Entity {
    private static final int TIME_TO_EXPLORE = 120;
    private int countdown = TIME_TO_EXPLORE;
    private Bomber owner;
    private boolean exploded = false;
    private FlameSegments flameSegments;
    private List<Image> bombAnimation = new ArrayList<>();

    private AudioClip explosionSound;
    private AudioClip tickSound; // THÊM biến AudioClip cho tiếng tick

    // CẬP NHẬT constructor để nhận cả hai AudioClip
    public Bomb(int x, int y, Bomber owner, AudioClip explosionSound, AudioClip tickSound) {
        super(x, y, Sprite.bomb.getFxImage());
        this.owner = owner;
        this.explosionSound = explosionSound;
        this.tickSound = tickSound; // GÁN âm thanh tick được truyền vào
        initAnimation();

        // BẮT ĐẦU PHÁT ÂM THANH TICK KHI BOMB ĐƯỢC TẠO
        if (this.tickSound != null) {
            this.tickSound.setCycleCount(AudioClip.INDEFINITE); // Phát lặp lại vô hạn
            this.tickSound.play();
            System.out.println("Bomb tick sound started!"); // Debug
        }
    }

    // Constructor cũ (có thể bỏ đi nếu tất cả các nơi gọi Bomb đều dùng constructor mới)
    public Bomb(int x, int y, Bomber owner) {
        this(x, y, owner, null, null); // Gọi constructor mới với cả hai âm thanh là null
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
                BombermanGame.removeEntity(this);
                owner.increaseBomb();
            }
        } else {
            if (flameSegments != null) {
                flameSegments.update();
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

        // DỪNG ÂM THANH TICK KHI BOM NỔ
        if (tickSound != null && tickSound.isPlaying()) {
            tickSound.stop();
            System.out.println("Bomb tick sound stopped!"); // Debug
        }

        // PHÁT ÂM THANH BOM NỔ
        if (explosionSound != null) {
            explosionSound.play();
            System.out.println("Bomb explosion sound played!"); // Debug
        }

        flameSegments = new FlameSegments(x, y, owner);
        // owner.decreaseBomb(); // Dòng này có vẻ dư hoặc sai logic
        BombermanGame.addEntity(flameSegments);
    }

    public boolean isExploded() {
        return exploded;
    }

    public Bomber getOwner() {
        return owner;
    }
}