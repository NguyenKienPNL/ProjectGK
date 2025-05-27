package uet.oop.bomberman.entities;

import javafx.scene.image.Image;

public abstract class Enemy extends Entity {
    protected int animate;
    protected static final int maxAnimate = 30;

    protected boolean isDead = false;
    public Enemy(int x, int y, Image img) {
        super(x, y, img);
    }
    public abstract void update();
    public abstract void move();

    public boolean isDead() {
        return isDead;
    }

    public void destroy() {
        isDead = true;
        animate = 0;
    }
}
