package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Balloom extends Enemy{
    private int direction; // -1 1

    public Balloom(int x, int y, Image img) {
        super(x, y, img);

        this.left_images.add(Sprite.balloom_left1.getFxImage());
        this.left_images.add(Sprite.balloom_left2.getFxImage());
        this.left_images.add(Sprite.balloom_left3.getFxImage());

        this.right_images.add(Sprite.balloom_right1.getFxImage());
        this.right_images.add(Sprite.balloom_right2.getFxImage());
        this.right_images.add(Sprite.balloom_right3.getFxImage());

        this.speed = 1;
        point = 10;
    }

    public void update() {
        this.x = Math.round((float)this.realX / Sprite.SCALED_SIZE);
        this.y = Math.round((float)this.realY / Sprite.SCALED_SIZE);
        if (isDead()) {
            animate++;
            if (animate <= maxAnimate) {
                img = Sprite.balloom_dead.getFxImage();
            } else {
                BombermanGame.removeEntity(this);
            }
        } else {
            move();
        }
    }

    public void move() {
        if (direction == -1) {
            setImg(getNextLeftImage());
            if (!moveWithCollision(-1, 0)) direction = 1;
        } else {
            setImg(getNextRightImage());
            if (!moveWithCollision(1, 0)) direction = -1;
        }
    }
}
