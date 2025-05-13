package uet.oop.bomberman.entities;

import javafx.scene.image.Image;

public class Grass extends Entity {
    private boolean canWalkThrough;
    private boolean canPlaceBomb;

    public Grass(int x, int y, Image img) {
        super(x, y, img);
        this.canPlaceBomb = true;
        this.canWalkThrough = true;
    }

    public boolean isCanPlaceBomb() {
        return canPlaceBomb;
    }

    public boolean isCanWalkThrough() {
        return canWalkThrough;
    }
    @Override
    public void update() {

    }

    public char getSymbol() {
        return ' ';
    }
}
