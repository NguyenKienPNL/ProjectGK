package uet.oop.bomberman.entities.Items;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Entity;

import java.util.spi.TimeZoneNameProvider;

public abstract class Item extends Entity {
    protected boolean visible = false;
    private int animationCounter = 0;
//    vat pham sau khi bom no
    public Item(int x, int y, Image img) {
        super(x, y, img);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean value) {
        this.visible = value;
    }

    protected Image getImageFrame1() {
        return img;
    }

    protected Image getImageFrame2() {
        return null;
    }
    @Override
    public void update() {
        if(visible) {
            animationCounter++;
            if((animationCounter / 20) % 2 == 0) {
                img = getImageFrame1();
            }
            else {
                img = getImageFrame2();
            }
        }
    }
    public abstract void applyEffect(uet.oop.bomberman.entities.Bomber bomber);
}
