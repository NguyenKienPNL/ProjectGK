package uet.oop.bomberman.engine;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.graphics.SpriteSheet;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.engine.LevelInfo;

import java.awt.event.KeyEvent;

import static javafx.scene.input.KeyCode.W;

public class Move {
    public static final int WIDTH = 30;
    public static final int HEIGHT = 15;

    private char[][] map;

    public Move(LevelInfo levelInfo) {
        this.map = levelInfo.map;
    }

    public boolean validate(int x, int y) {
        return (1 <= x && x < WIDTH - 1 && 1 <= y && y < HEIGHT - 1 && this.map[x][y] != '#');
    }

    public int moveUp(int y) {return y - 1;};

    public int moveDown(int y) {return y + 1;};

    public int moveLeft(int x) {return x - 1;};

    public int moveRight(int x) {return x + 1;};

    public void controlledMove(Scene scene, Entity entity) {
    }
}
