package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemDistribute extends Entity {
    public class Point {
        public int x;
        public int y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final int dropFrame = 450;
    private ArrayList<Point> points = new ArrayList<Point>();
    private int frame = 0;
    private Random rand = new Random();

    public ItemDistribute(int x, int y, Image img) {
        super(x, y, img);
    }

    public void update() {
        frame++;
        if (frame >= dropFrame) {
            frame = 0;
            findAvailablePoints();
            System.out.println(points.size());
            addAPowerUp();
            points.clear();
        }
    }

    public void findAvailablePoints() {
        List<Entity> objects = BombermanGame.getStillObjects();
        for (int i = 0; i < objects.size(); i++) {
            if (!BombermanGame.hasPlayerOrEnemyAt(objects.get(i).getX(), objects.get(i).getY())
            && !(objects.get(i) instanceof Wall)) {
                points.add(new Point(objects.get(i).getX(), objects.get(i).getY()));
            }
        }
    }

    public void addAPowerUp() {
        if (points.isEmpty()) {
            return;
        }
        int type = rand.nextInt(3);
        Point point = points.get(rand.nextInt(points.size()));
//        System.out.println(type);
        switch (type) {
            case 0:
                BombermanGame.addEntity(new FlameItem(point.x, point.y, Sprite.powerup_flames.getFxImage()));
                break;
            case 1:
                BombermanGame.addEntity(new BombItem(point.x, point.y, Sprite.powerup_bombs.getFxImage()));
                break;
            case 2:
                BombermanGame.addEntity(new SpeedItem(point.x, point.y, Sprite.powerup_speed.getFxImage()));
                break;
        }
    }
}
