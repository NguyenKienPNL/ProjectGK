package uet.oop.bomberman.entities;

import uet.oop.bomberman.BombermanGame;
import javafx.scene.image.Image;

public class Brick extends Entity {
    private boolean destroyed = false;
    private int animate = 0;
//    thoi gian gach co hieu ung no
    private static final int maxanimate = 30;
    private static final int animateDuration = 10;
//    them hieu ung no
    private Image[] explosionFrames = new Image[] {
            new Image("sprites/brick_exploded.png"),
             new Image("sprites/brick_exploded1.png"),
              new Image("sprites/brick_exploded2.png")
    };

    public Brick(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
//        khi gach no
        if(destroyed && animate <= maxanimate) {
            animate++;
            img = explosionFrames[(animate / animateDuration) % 3];
        }

        if (destroyed && animate >= maxanimate) {
            if (x == BombermanGame.portalX && y == BombermanGame.portalY) {
                BombermanGame.portal.setHidden(false);
            }
            BombermanGame.map[y][x] = ' ';
            BombermanGame.removeEntity(this);
        }
    }


    public boolean isDestroyed() {
        return destroyed;
    }

//    khi bom no thi .ham nay
    public void destroy() {
        destroyed = true;
        animate = 0;
    }

}
