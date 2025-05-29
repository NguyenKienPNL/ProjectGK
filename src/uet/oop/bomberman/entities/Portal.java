package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

public class Portal extends Entity {
    private boolean isHidden;
    public Portal(int x, int y, Image img) {
        super(x, y, img);
        this.isHidden = true;
    }

    public Portal(int x, int y, Image img, boolean isHidden) {
        super(x, y, img);
        this.isHidden = isHidden;
    }

    public void update() {
        if (isHidden()) {
            img = null;
        } else {
            img = Sprite.portal.getFxImage();
        }
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
