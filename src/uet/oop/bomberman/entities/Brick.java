package uet.oop.bomberman.entities;

import uet.oop.bomberman.entities.Items.Item;
import javafx.scene.image.Image;

public class Brick extends Entity {
    private boolean destroyed = false;
    private int animate = 0;
//    thoi gian gach co hieu ung no
    private static final int maxanimate = 30;

//    them hieu ung no
    private Image[] explosionFrames = new Image[] {
            new Image("sprites/brick_exploded.png"),
             new Image("sprites/brick_exploded1.png"),
              new Image("sprites/brick_exploded.png")
    };

    public Brick(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void update() {
//        khi gach no
        if(destroyed && animate < maxanimate) {
            animate++;
            int frame = animate / 10;
//            hieu ung no
            if(frame < explosionFrames.length) {
                img = explosionFrames[frame];
            } else {
                img = null;
            }
        }
    }



//    khi bom no thi .ham nay
    public void destroy() {
        destroyed = true;
        animate = 0;
    }


}
