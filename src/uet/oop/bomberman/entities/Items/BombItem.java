package uet.oop.bomberman.entities.Items;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Bomber;
public class BombItem extends Item {
    public BombItem(int x, int y, Image img) {
        super(x, y, img);
    }
     @Override
    public void applyEffect(Bomber bomber) {
        bomber.increaseBomb();
        bomber.setBombBufftime(bomber.BUFF);
     }
}
