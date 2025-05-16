package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public class Flame extends Entity {
    public Flame(int x, int y, Image img) {
        super(x, y, img);

        up_images.add(Sprite.explosion_vertical)
    }

    public void update() {

    }
}
