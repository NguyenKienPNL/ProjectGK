package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;

import java.util.List;

public abstract class Enemy extends Entity {
    protected int animate;
    protected int point;
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

    public int getPoint() {
        return point;
    }

    public boolean closeToBomb() {
        List<Entity> entityList = BombermanGame.entities;
        for (Entity entity : entityList) {
            if (entity instanceof Bomb && BombermanGame.nearTo(realX, realY, entity.getRealX(), entity.getRealY())) {
                return true;
            }
        }
        return false;
    }
}
