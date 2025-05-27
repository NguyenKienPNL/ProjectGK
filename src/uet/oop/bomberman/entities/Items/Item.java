package uet.oop.bomberman.entities.Items;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.entities.Entity;

import java.util.spi.TimeZoneNameProvider;

public abstract class Item extends Entity {
    private static int lifeFrame = 300;
    private int frame = 0;
    private boolean isPickedUp = false;
    public Item(int x, int y, Image img) {
        super(x, y, img);
    }

    protected Image getImageFrame1() {
        return img;
    }

    protected Image getImageFrame2() {
        return null;
    }
    @Override
    public void update() {
        frame++;
        if (frame >= lifeFrame || isPickedUp()) {
            BombermanGame.removeEntity(this);
        }
    }
    public abstract void applyEffect(uet.oop.bomberman.entities.Bomber bomber);

    public boolean isPickedUp() {
        return isPickedUp;
    }

    public void pickUp() {
        isPickedUp = true;
    }
}
