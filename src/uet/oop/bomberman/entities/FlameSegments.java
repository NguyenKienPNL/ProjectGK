package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlameSegments extends Entity {
    private static int DISPLAY_TIME = 120;
    private int countdown = DISPLAY_TIME;
    private List<Flame> flames = new ArrayList<>();
    private List<Entity> destroyEntities = new ArrayList<>();
    private Bomber bomber;

    public FlameSegments(int x, int y, Bomber bomber) {
        super(x, y, Sprite.bomb_exploded.getFxImage());
        this.bomber = bomber;

        // Flame ở vị trí bomb
        flames.add(new Flame(x, y, Sprite.bomb_exploded.getFxImage(), false, -1,
                getImages(-1, false)));

        // 4 hướng
        addDirection(-1, 0, 0); // left
        addDirection(1, 0, 1);  // right
        addDirection(0, -1, 2); // up
        addDirection(0, 1, 3);  // down

        BombermanGame.entities.addAll(flames);
    }

    public void update() {
        animate();

        if (isFinished()) {
            destroy();
            System.out.println(destroyEntities.size());
            bomber.increaseBomb();
            BombermanGame.removeEntity(this);
        }
    }

    public List<Flame> getFlames() {
        return flames;
    }

    public void animate() {
        int frame = (DISPLAY_TIME - countdown) / 40;
        for (Flame flame : flames) {
            if (!flame.isFinished()) flame.getFrame(frame);
        }
    }

    private void addDirection(int dx, int dy, int direction) {
        int curX = x;
        int curY = y;

        for (int i = 1; i <= bomber.getBombRadius(); i++) {
            curX += dx;
            curY += dy;

            // Nếu có Wall, dừng luôn
            if (BombermanGame.hasObstacleAt(curX, curY)) {
                break;
            }

            boolean isLast = (i == bomber.getBombRadius());

            // Nếu gặp Brick, thêm vào danh sách phá và ngừng luôn
            if (BombermanGame.hasDestructibleAt(curX, curY)) {
                destroyEntities.addAll(BombermanGame.getEntitiesAt(curX, curY));
                break;
            }

            // Thêm Flame vào list
//            flames.add(new Flame(curX, curY, Sprite.explosion_horizontal.getFxImage(), isLast, direction));
            flames.add(new Flame(curX, curY, Sprite.explosion_horizontal2.getFxImage(),
                    isLast, direction, getImages(direction, isLast)));

            // Nếu gặp player/enemy
            if (BombermanGame.hasPlayerOrEnemyAt(curX, curY)) {
                destroyEntities.addAll(BombermanGame.getEntitiesAt(curX, curY));
            }
        }
    }

    public List<Image> getImages(int direction, boolean isLast) {
        List<Image> images = new ArrayList<>();
        if (isLast) {
            if (direction == 0) {
                images.add(Sprite.explosion_horizontal_left_last.getFxImage());
                images.add(Sprite.explosion_horizontal_left_last1.getFxImage());
                images.add(Sprite.explosion_horizontal_left_last2.getFxImage());
            } else if (direction == 1) {
                images.add(Sprite.explosion_horizontal_right_last.getFxImage());
                images.add(Sprite.explosion_horizontal_right_last1.getFxImage());
                images.add(Sprite.explosion_horizontal_right_last2.getFxImage());
            } else if (direction == 2) {
                images.add(Sprite.explosion_vertical_top_last.getFxImage());
                images.add(Sprite.explosion_vertical_top_last1.getFxImage());
                images.add(Sprite.explosion_vertical_top_last2.getFxImage());
            } else {
                images.add(Sprite.explosion_vertical_down_last.getFxImage());
                images.add(Sprite.explosion_vertical_down_last1.getFxImage());
                images.add(Sprite.explosion_vertical_down_last2.getFxImage());
            }
        } else {
            if (direction == 0 || direction == 1) {
                images.add(Sprite.explosion_horizontal.getFxImage());
                images.add(Sprite.explosion_horizontal1.getFxImage());
                images.add(Sprite.explosion_horizontal2.getFxImage());
            } else if (direction == 2 || direction == 3) {
                images.add(Sprite.explosion_vertical.getFxImage());
                images.add(Sprite.explosion_vertical1.getFxImage());
                images.add(Sprite.explosion_vertical2.getFxImage());
            } else {
                images.add(Sprite.bomb_exploded.getFxImage());
                images.add(Sprite.bomb_exploded1.getFxImage());
                images.add(Sprite.bomb_exploded2.getFxImage());
            }
        }
        return images;
    }

    public void destroy() {
        for (Entity entity : destroyEntities) {
//            entity.loadDead(); // hàm này bạn tự xử lý animation chết
            if (entity instanceof Brick) {
                ((Brick) entity).destroy();
            } else if (entity instanceof Bomber) {
                ((Bomber) entity).destroy();
            } else {
                ((Enemy) entity).destroy();
            }
        }
    }

    public boolean isFinished() {
        for (Flame flame : flames) {
            if (!flame.isFinished()) {
                return false;
            }
        }
        return true;
    }
}
