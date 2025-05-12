package uet.oop.bomberman.entities;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;

import java.util.concurrent.atomic.AtomicInteger;

public class Bomber extends Entity {

    public Bomber(int x, int y, Image img) {
        super( x, y, img);
    }

    @Override
    public void update() {

    }

    public void bomberMove(Scene scene, Entity bomberman) {
        AtomicInteger cnt = new AtomicInteger();
        AtomicInteger prevType = new AtomicInteger();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    if (prevType.get() != 1) {
                        cnt.set(0);
                    }
                    if (cnt.get() % 3 == 0) bomberman.setImg(Sprite.player_up.getFxImage());
                    else if (cnt.get() % 3 == 1) bomberman.setImg(Sprite.player_up_1.getFxImage());
                    else bomberman.setImg(Sprite.player_up_2.getFxImage());
                    cnt.incrementAndGet();
                    prevType.set(1);
                    bomberman.setY(bomberman.getY() - 1);
                    break;
                case S:
                    if (prevType.get() != 2) {
                        cnt.set(0);
                    }
                    if (cnt.get() % 3 == 0) bomberman.setImg(Sprite.player_down.getFxImage());
                    else if (cnt.get() % 3 == 1) bomberman.setImg(Sprite.player_down_1.getFxImage());
                    else bomberman.setImg(Sprite.player_down_2.getFxImage());
                    cnt.incrementAndGet();
                    prevType.set(2);
                    bomberman.setY(bomberman.getY() + 1);
                    break;
                case A:
                    if (prevType.get() != 3) {
                        cnt.set(0);
                    }
                    if (cnt.get() % 3 == 0) bomberman.setImg(Sprite.player_left.getFxImage());
                    else if (cnt.get() % 3 == 1) bomberman.setImg(Sprite.player_left_1.getFxImage());
                    else bomberman.setImg(Sprite.player_left_2.getFxImage());
                    cnt.incrementAndGet();
                    prevType.set(3);
                    bomberman.setX(bomberman.getX() - 1);
                    break;
                case D:
                    if (prevType.get() != 4) {
                        cnt.set(0);
                    }
                    if (cnt.get() % 3 == 0) bomberman.setImg(Sprite.player_right.getFxImage());
                    else if (cnt.get() % 3 == 1) bomberman.setImg(Sprite.player_right_1.getFxImage());
                    else bomberman.setImg(Sprite.player_right_2.getFxImage());
                    cnt.incrementAndGet();
                    prevType.set(4);
                    bomberman.setX(bomberman.getX() + 1);
                    break;
                default:
                    break;
            }
        });
    }

    public char getSymbol() {
        return 'p';
    }
}
