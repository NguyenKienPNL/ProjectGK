package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Flame extends Entity {
    private static final int maxFrames = 120;
    private List<Image> explodeList = new ArrayList<>();
    private int direction;
    private boolean isLast;
    private int frameCount;
    private int frameIndex;



    public Flame(int x, int y, Image img) {
        super(x, y, img);

    }

    public Flame(int x, int y, Image img, boolean isLast, int direction, List<Image> explodeList) {
        super(x, y, img);
        this.explodeList = explodeList;
        this.frameCount = 0;
        this.isLast = isLast;
        this.direction = direction;
    }

    public void update() {
        frameCount++;
        if (isFinished()) {
            BombermanGame.removeEntity(this);
        }
    }

    public void getFrame(int index) {
        img = explodeList.get(index);
    }

    public boolean isFinished() {
        return frameCount >= maxFrames;
    }
}
