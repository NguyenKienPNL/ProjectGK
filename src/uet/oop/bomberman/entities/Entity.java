package uet.oop.bomberman.entities;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uet.oop.bomberman.graphics.Sprite;

public abstract class Entity {
    //Tọa độ X tính từ góc trái trên trong Canvas
    protected int x;

    //Tọa độ Y tính từ góc trái trên trong Canvas
    protected int y;

    //Tọa độ thực

    protected int realX;
    protected int realY;

    protected Image img;

    //Khởi tạo đối tượng, chuyển từ tọa độ đơn vị sang tọa độ trong canvas
    public Entity( int xUnit, int yUnit, Image img) {
        this.x = xUnit;
        this.y = yUnit;
        this.realX = xUnit * Sprite.SCALED_SIZE;
        this.realY = yUnit * Sprite.SCALED_SIZE;
        this.img = img;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(img, getRealX(), getRealY());
    }
    public abstract void update();

    public abstract char getSymbol();

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
        return x * Sprite.SCALED_SIZE;
    }

    public void setRealX(int realX) {
        this.realX = realX;
    }

    public int getRealY() {
        return y * Sprite.SCALED_SIZE;
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
}
