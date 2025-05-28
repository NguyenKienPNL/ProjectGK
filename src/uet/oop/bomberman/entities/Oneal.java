package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.BombermanGame;

import java.util.*;

public class Oneal extends Enemy {

    private int radius;
    private int direction; // 0: left, 1: right, 2: up, 3: down
    private boolean sprinted;

    private Random rand = new Random();
    private char[][] map;

    public Oneal(int x, int y, Image img) {
        super(x, y, img);

        this.left_images.add(Sprite.oneal_left1.getFxImage());
        this.left_images.add(Sprite.oneal_left2.getFxImage());
        this.left_images.add(Sprite.oneal_left3.getFxImage());

        this.right_images.add(Sprite.oneal_right1.getFxImage());
        this.right_images.add(Sprite.oneal_right2.getFxImage());
        this.right_images.add(Sprite.oneal_right3.getFxImage());

        this.speed = 1;
        this.radius = 10 * Sprite.SCALED_SIZE;
        this.direction = 0;
        this.sprinted = false;
        point = 20;
//        map = BombermanGame.map;
    }

    @Override
    public void update() {
//        if (BombermanGame.nearTo(realX, realY))
        if (isDead()) {
            animate++;
            if (animate <= maxAnimate) {
                img = Sprite.oneal_dead.getFxImage();
            } else {
                BombermanGame.removeEntity(this);
            }
        } else {
            move();
        }
    }


    public void move() {

        //cap nhat toc do
        if (!sprinted && nearToBomberman()) {
            sprinted = true;
//            speed += 1;
        }

        if (sprinted && !nearToBomberman()) {
            sprinted = false;
//            speed -= 1;
        }

        // cac huong kha di (neu gan)
        if (nearToBomberman()) {
            if (isCentered()) {
                int nextDirection = findDirection();
                if (nextDirection != -1) {
                    direction = nextDirection;
                } else {
                    chooseARandomDirection();
                }
            }

            if (!canGo(direction)) {
                int nextDirection = findDirection();
                if (nextDirection != -1) {
                    direction = nextDirection;
                } else {
                    chooseARandomDirection();
                }
            }
        } else {
            if (isCentered() && !canGo(direction)) {
                chooseARandomDirection();
            }
        }


        switch (direction) {
            case 0:
                setImg(getNextLeftImage());
                moveWithCollision(-1, 0);
//                moveToCell(-1, 0);
                break;
            case 1:
                setImg(getNextRightImage());
                moveWithCollision(1, 0);
//                moveToCell(1, 0);
                break;
            case 2:
                setImg(getNextLeftImage());
                moveWithCollision(0, -1);
//                moveToCell(0, -1);
                break;
            case 3:
                setImg(getNextRightImage());
                moveWithCollision(0, 1);
//                moveToCell(0, 1);
                break;
        }
    }

    public boolean nearToBomberman() {
        return distance() <= radius * radius;
    }


    public boolean isCentered() {
        int epsilon = 2;
        int cellCenterX = getXFromRealX(realX) * Sprite.SCALED_SIZE + Sprite.SCALED_SIZE / 2;
        int cellCenterY = getYFromRealY(realY) * Sprite.SCALED_SIZE + Sprite.SCALED_SIZE / 2;

        return Math.abs(realX + Sprite.SCALED_SIZE / 2 - cellCenterX) < epsilon
                && Math.abs(realY + Sprite.SCALED_SIZE / 2 - cellCenterY) < epsilon;
    }

    public void chooseARandomDirection() {
        int tileX = getXFromRealX(realX);
        int tileY = getYFromRealY(realY);
        List<Integer> possibleDirections = new ArrayList<>();
        if (BombermanGame.validate(tileX - 1, tileY)) possibleDirections.add(0);
        if (BombermanGame.validate(tileX + 1, tileY)) possibleDirections.add(1);
        if (BombermanGame.validate(tileX, tileY - 1)) possibleDirections.add(2);
        if (BombermanGame.validate(tileX, tileY + 1)) possibleDirections.add(3);

        direction = possibleDirections.get(rand.nextInt(possibleDirections.size()));
    }

    public int findDirection() {
        int rows = BombermanGame.map.length;
        int cols = BombermanGame.map[0].length;

        boolean[][] visited = new boolean[rows][cols];
        int[][] prevDir = new int[rows][cols];

        Queue<int[]> queue = new LinkedList<>();

        int startX = getXFromRealX(realX);
        int startY = getYFromRealY(realY);

        int targetX = getXFromRealX(BombermanGame.getBomberman().getRealX());
        int targetY = getYFromRealY(BombermanGame.getBomberman().getRealY());

        if (startX == targetX && startY == targetY) return -1;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        queue.add(new int[]{startX, startY});
        visited[startY][startX] = true;
        prevDir[startY][startX] = -1;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int curX = current[0];
            int curY = current[1];

            for (int dir = 0; dir < 4; dir++) {
                int newX = curX + dx[dir];
                int newY = curY + dy[dir];

                if (newX >= 0 && newX < cols && newY >= 0 && newY < rows
                        && !visited[newY][newX]
                        && BombermanGame.map[newY][newX] != '*'
                        && BombermanGame.map[newY][newX] != '#'
                        && BombermanGame.map[newY][newX] != 'b') {

                    queue.add(new int[]{newX, newY});
                    visited[newY][newX] = true;

                    // lưu hướng di chuyển để biết đi từ đâu đến
                    prevDir[newY][newX] = dir;

                    // nếu đến đích thì truy vết ngược về
                    if (newX == targetX && newY == targetY) {
                        int traceX = newX;
                        int traceY = newY;

                        while (prevDir[traceY][traceX] != -1) {
                            int backDir = prevDir[traceY][traceX];
                            traceX -= dx[backDir];
                            traceY -= dy[backDir];

                            if (traceX == startX && traceY == startY) {
                                return backDir;
                            }
                        }
                    }
                }
            }
        }

        return -1; // không tìm thấy
    }

   public boolean canGo(int direction) {
        int dx, dy;
        int tileX = getXFromRealX(realX);
        int tileY = getYFromRealY(realY);
        switch (direction) {
            case 0:
                dx = -1;
                dy = 0;
                break;
            case 1:
                dx = 1;
                dy = 0;
                break;
            case 2:
                dx = 0;
                dy = -1;
                break;
            default:
                dx = 0;
                dy = 1;
                break;
        }

//        if (BombermanGame.hasObstacleAt(tileX + dx, tileY + dy)
//        || BombermanGame.hasDestructibleAt(tileX + dx, tileY + dy)
//        || BombermanGame.hasBlockingBombAt(tileX + dx, tileY + dy)) return false;
//        return true;
       if (!moveWithCollision(dx, dy)) return false;
       return true;
   }

    int distance() {
        return (int) (Math.pow(BombermanGame.getBomberman().getRealX() - realX, 2)
                 + Math.pow(BombermanGame.getBomberman().getRealY() - realY, 2));
    }
}
