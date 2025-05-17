package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Flame extends Entity {
    private List<Image> explodeUp = new ArrayList<>();
    private List<Image> explodeDown = new ArrayList<>();
    private List<Image> explodeLeft = new ArrayList<>();
    private List<Image> explodeRight = new ArrayList<>();
    private List<Image> explodeCenter = new ArrayList<>();

    private int frameSpeed = 5;
    private int frameCount = 0;
    private boolean isLast;
    private int direction; // 0: left, 1: right, 2: up, 3: down, -1: center
    private int frameIndex;
    private int frameShowed;

    public Flame(int x, int y, Image img) {
        super(x, y, img);

    }

    public Flame(int x, int y, Image img, boolean isLast, int direction) {
        super(x, y, img);
        this.isLast = isLast;
        this.direction = direction;
        this.frameIndex = 0;
        this.frameShowed = 0;

        up_images.add(Sprite.explosion_vertical.getFxImage());
        up_images.add(Sprite.explosion_vertical1.getFxImage());
        up_images.add(Sprite.explosion_vertical2.getFxImage());

        down_images.add(Sprite.explosion_vertical.getFxImage());
        down_images.add(Sprite.explosion_vertical1.getFxImage());
        down_images.add(Sprite.explosion_vertical2.getFxImage());

        left_images.add(Sprite.explosion_horizontal.getFxImage());
        left_images.add(Sprite.explosion_horizontal1.getFxImage());
        left_images.add(Sprite.explosion_horizontal2.getFxImage());

        right_images.add(Sprite.explosion_horizontal.getFxImage());
        right_images.add(Sprite.explosion_horizontal1.getFxImage());
        right_images.add(Sprite.explosion_horizontal2.getFxImage());

        explodeUp.add(Sprite.explosion_vertical_top_last.getFxImage());
        explodeUp.add(Sprite.explosion_vertical_top_last1.getFxImage());
        explodeUp.add(Sprite.explosion_vertical_top_last2.getFxImage());

        explodeDown.add(Sprite.explosion_vertical_down_last.getFxImage());
        explodeDown.add(Sprite.explosion_vertical_down_last1.getFxImage());
        explodeDown.add(Sprite.explosion_vertical_down_last2.getFxImage());

        explodeLeft.add(Sprite.explosion_horizontal_left_last.getFxImage());
        explodeLeft.add(Sprite.explosion_horizontal_left_last1.getFxImage());
        explodeLeft.add(Sprite.explosion_horizontal_left_last2.getFxImage());

        explodeRight.add(Sprite.explosion_horizontal_right_last.getFxImage());
        explodeRight.add(Sprite.explosion_horizontal_right_last1.getFxImage());
        explodeRight.add(Sprite.explosion_horizontal_right_last2.getFxImage());

        explodeCenter.add(Sprite.bomb_exploded.getFxImage());
        explodeCenter.add(Sprite.bomb_exploded1.getFxImage());
        explodeCenter.add(Sprite.bomb_exploded2.getFxImage());
    }

    public void update() {
        frameCount++;
        if (frameCount >= frameSpeed) {
            frameCount = 0;
            frameShowed++;

            switch (direction) {
                case -1:
                    img = explodeLeft.get(frameIndex);
                    break;
                case 0:
                    if (isLast) {
                        img = explodeLeft.get(frameIndex);
                    } else {
                        img = left_images.get(frameIndex);
                    }
                    break;
                case 1:
                    if (isLast) {
                        img = explodeRight.get(frameIndex);
                    } else {
                        img = right_images.get(frameIndex);
                    }
                    break;
                case 2:
                    if (isLast) {
                        img = explodeUp.get(frameIndex);
                    } else {
                        img = up_images.get(frameIndex);
                    }
                    break;
                case 3:
                    if (isLast) {
                        img = explodeDown.get(frameIndex);
                    } else {
                        img = down_images.get(frameIndex);
                    }
                    break;
            }

            frameIndex++;
        }
    }
}
