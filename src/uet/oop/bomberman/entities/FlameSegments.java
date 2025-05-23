package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlameSegments extends Entity {
    private List<Flame> flames = new ArrayList<>();
    private List<Entity> destroyEntities = new ArrayList<>();
    private Bomber bomber;

    public FlameSegments(int x, int y, Bomber bomber) {
        super(x, y, Sprite.bomb_exploded.getFxImage());
        this.bomber = bomber;

        // Flame ở vị trí bomb
        flames.add(new Flame(x, y, Sprite.bomb_exploded.getFxImage(), false, -1));

        // 4 hướng
        addDirection(-1, 0, 0); // left
        addDirection(1, 0, 1);  // right
        addDirection(0, -1, 2); // up
        addDirection(0, 1, 3);  // down

        System.out.println(destroyEntities.size());
        destroy();
    }

    public void update() {
        Iterator<Flame> iter = flames.iterator();
        while (iter.hasNext()) {
            Flame fSeg = iter.next();
            fSeg.update();
            if (fSeg.isFinished()) {
                iter.remove(); // Xoá khỏi list
            }
        }

        if (isFinished()) {
//            BombermanGame.removeFlame(this);
            destroy();
        }
    }

    public List<Flame> getFlames() {
        return flames;
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
                destroyEntities.add(BombermanGame.getStillObjectAt(curX, curY));
                break;
            }

            // Thêm Flame vào list
            flames.add(new Flame(curX, curY, Sprite.explosion_horizontal.getFxImage(), isLast, direction));


            // Nếu gặp player/enemy
            if (BombermanGame.hasPlayerOrEnemyAt(curX, curY)) {
                destroyEntities.addAll(BombermanGame.getEntitiesAt(curX, curY));
            }
        }
    }

    public void destroy() {
        for (Entity entity : destroyEntities) {
//            entity.loadDead(); // hàm này bạn tự xử lý animation chết
            if (entity instanceof Brick) {
                ((Brick) entity).destroy();
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
