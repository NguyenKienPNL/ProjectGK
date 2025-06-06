package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Bomber;

public class SpeedItem extends Item {
    public SpeedItem(int x, int y, Image img) {
        super(x, y, img);
    }

    @Override
    public void applyEffect(Bomber bomber) {
        bomber.increaseSpeed();
        bomber.setSpeedBufftime(bomber.BUFF);
    }
}
