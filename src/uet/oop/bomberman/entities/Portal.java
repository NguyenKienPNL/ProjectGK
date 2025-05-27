package uet.oop.bomberman.entities;

import javafx.scene.image.Image;

public class Portal extends Entity {
    private boolean isHidden;
    public Portal(int x, int y, Image img) {
        super(x, y, img);
        this.isHidden = true;
    }

    public void update() {

    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
