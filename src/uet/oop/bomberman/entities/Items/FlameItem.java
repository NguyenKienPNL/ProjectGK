package uet.oop.bomberman.entities.Items;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Bomber;

public class FlameItem extends Item {
    private final Image frame1 = new Image("sprites/powerup_flames.png");

    public FlameItem(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    protected Image getImageFrame1() {
        return frame1;
    }

    @Override
    protected Image getImageFrame2() {
        return null;
    }

    @Override
    public void applyEffect(Bomber bomber) {
        bomber.increaseFlameLength();
        bomber.setFlameBufftime(bomber.BUFF);
    }
}
