package uet.oop.bomberman.entities;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

public class Bomb extends Entity {
    private static final int TIME_TO_EXPLORE = 120;
    private int countdown = TIME_TO_EXPLORE;
    private Bomber owner;
    private boolean exploded = false;
    private FlameSegments flameSegments;
    private List<Image> bombAnimation = new ArrayList<>();

    public Bomb(int x, int y, Bomber owner) {
        super(x, y, Sprite.bomb.getFxImage());
        this.owner = owner;
        initAnimation();
    }

    private void initAnimation() {
        bombAnimation.add(Sprite.bomb.getFxImage());
        bombAnimation.add(Sprite.bomb_1.getFxImage());
        bombAnimation.add(Sprite.bomb_2.getFxImage());
    }

    @Override
    public void update() {
        if(!exploded) {
            countdown--;
            animate();
            if (countdown <= 0) {
                exploded();
                BombermanGame.removeEntity(this);
                owner.increaseBomb();
            }
        } else {
            flameSegments.update();
        }
    }

    private void animate() {
        int frame = (TIME_TO_EXPLORE - countdown) / 20;
        if(frame < bombAnimation.size()) {
            img = bombAnimation.get(frame);
        }
    }

    public void exploded() {
        exploded = true;
        img = Sprite.bomb_exploded.getFxImage();
        flameSegments = new FlameSegments(x, y, owner );
        owner.decreaseBomb();
        BombermanGame.addEntity(flameSegments);
    }
    public boolean isExploded() {
        return exploded;
    }

    public Bomber getOwner() {
        return owner;
    }
}