package uet.oop.bomberman.entities;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.BombermanGame;

import java.util.ArrayList;

import static uet.oop.bomberman.BombermanGame.WIDTH;
import static uet.oop.bomberman.BombermanGame.HEIGHT;

public abstract class Entity {
    //Tọa độ X tính từ góc trái trên trong Canvas
    protected int x;

    //Tọa độ Y tính từ góc trái trên trong Canvas
    protected int y;

    //Tọa độ thực

    protected int realX;
    protected int realY;

    protected Image img;
    protected ArrayList<Image> left_images = new ArrayList<>();
    protected ArrayList<Image> right_images = new ArrayList<>();
    protected ArrayList<Image> up_images = new ArrayList<>();
    protected ArrayList<Image> down_images = new ArrayList<>();
    protected ArrayList<Image> dead_images = new ArrayList<>();

    protected int currentDirection;
    protected int previousDirection;
    protected int frameIndex;
    protected int speed;

    //Khởi tạo đối tượng, chuyển từ tọa độ đơn vị sang tọa độ trong canvas
    public Entity( int xUnit, int yUnit, Image img) {
        this.x = xUnit;
        this.y = yUnit;
        this.realX = xUnit * Sprite.SCALED_SIZE;
        this.realY = yUnit * Sprite.SCALED_SIZE;
        this.img = img;
        this.currentDirection = 0;
        this.previousDirection = -1;
    }

    public Entity(int xUnit, int yUnit, int speed, Image img, ArrayList<Image> left_images,
                  ArrayList<Image> right_images, ArrayList<Image> up_images, ArrayList<Image> down_images, ArrayList<Image> dead_images) {
        this.x = xUnit;
        this.y = yUnit;
        this.realX = xUnit * Sprite.SCALED_SIZE;
        this.realY = yUnit * Sprite.SCALED_SIZE;
        this.img = img;
        this.left_images = left_images;
        this.right_images = right_images;
        this.up_images = up_images;
        this.down_images = down_images;
        this.dead_images = dead_images;
        this.currentDirection = 0;
        this.previousDirection = -1;
        this.speed = speed;
        this.frameIndex = 0;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(img, getRealX(), getRealY());
    }
    public abstract void update();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.realX = x * Sprite.SCALED_SIZE;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.realY = y * Sprite.SCALED_SIZE;
    }

    public int getRealX() {
        return realX;
    }

    public void setRealX(int realX) {
        this.realX = realX;
    }

    public int getRealY() {
        return realY;
    }

    public void setRealY(int realY) {
        this.realY = realY;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public int getXFromRealX(int RealX) {
        return RealX / Sprite.SCALED_SIZE;
    }

    public int getYFromRealY(int RealY) {
        return RealY / Sprite.SCALED_SIZE;
    }

    public Image getNextLeftImage() {
        if (currentDirection != previousDirection) {
           frameIndex = 0;
        }
        frameIndex = (frameIndex + 1) % left_images.size();
        previousDirection = currentDirection;
        currentDirection = 1;
        return left_images.get(frameIndex);
    }

    public Image getNextRightImage() {
        if (currentDirection != previousDirection) {
            frameIndex = 0;
        }
        frameIndex = (frameIndex + 1) % right_images.size();
        previousDirection = currentDirection;
        currentDirection = 2;
        return right_images.get(frameIndex);
    }

    public Image getNextUpImage() {
        if (currentDirection != previousDirection) {
            frameIndex = 0;
        }
        frameIndex = (frameIndex + 1) % up_images.size();
        previousDirection = currentDirection;
        currentDirection = 3;
        return up_images.get(frameIndex);
    }

    public Image getNextDownImage() {
        if (currentDirection != previousDirection) {
            frameIndex = 0;
        }
        frameIndex = (frameIndex + 1) % down_images.size();
        previousDirection = currentDirection;
        currentDirection = 4;
        return down_images.get(frameIndex);
    }

    protected boolean moveWithCollision(int dx, int dy) {
        boolean moved = false;
        for (int i = 0; i < speed; i++) {
            if (BombermanGame.validatePixelMove(realX + dx, realY + dy)) {
                realX += dx;
                realY += dy;
                moved = true;
            } else {
                break;
            }
        }
        return moved;
    }

    protected boolean isMoveWithCollision(int dx, int dy) {
        boolean moved = false;
        int Rx = getRealX();
        int Ry = getRealY();
        for (int i = 0; i < speed; i++) {
            if (BombermanGame.validatePixelMove(Rx + dx, Ry + dy)) {
                Rx += dx;
                Ry += dy;
                moved = true;
            } else break;
        }
        return moved;
    }

    protected void align(int dx, int dy) {
        if (dx != 0) { // đi ngang → căn trục dọc
            int remainder = realY % Sprite.SCALED_SIZE;
            if (remainder != 0) {
                if (remainder < Sprite.SCALED_SIZE / 2) {
                    realY -= remainder;
                } else {
                    realY += (Sprite.SCALED_SIZE - remainder);
                }
            }
        }

        if (dy != 0) { // đi dọc → căn trục ngang
            int remainder = realX % Sprite.SCALED_SIZE;
            if (remainder != 0) {
                if (remainder < Sprite.SCALED_SIZE / 2) {
                    realX -= remainder;
                } else {
                    realX += (Sprite.SCALED_SIZE - remainder);
                }
            }
        }
    }

}
