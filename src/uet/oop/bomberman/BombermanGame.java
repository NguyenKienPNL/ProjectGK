package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.engine.LevelLoader;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BombermanGame extends Application {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 15;

    private GraphicsContext gc;
    private Canvas canvas;

    // List động và tĩnh tách riêng
    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();

    // Bomber tham chiếu riêng nếu cần dùng trực tiếp
    private Bomber bomberman;

    @Override
    public void start(Stage stage) throws IOException {
        // Canvas setup
        stage.setTitle("Bomberman");
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        Group root = new Group();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Load map và entity
        createMap();

        // Thêm Bomber vào entities
        for (Entity e : entities) {
            if (e instanceof Bomber) {
                bomberman = (Bomber) e;
            }
        }
        bomberman.handleKeyEvent(scene);

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        timer.start();
    }

    public void createMap() throws IOException {
        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level2.txt");

        // Load object tĩnh
        stillObjects = levelLoader.loadStillObjects(levelInfo);

        // Load các entity động
        List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
        entities.addAll(loadedEntities);
    }

    public void update() {
        // Update toàn bộ các entity động
        for (Entity entity : entities) {
            entity.update();
        }
    }

    public void render() {
        // Clear canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Vẽ object tĩnh
        for (Entity entity : stillObjects) {
            entity.render(gc);
        }

        // Vẽ entity động
        for (Entity entity : entities) {
            entity.render(gc);
        }
    }

    // Hàm kiểm tra vật cản (Wall, Brick)
    public static boolean hasObstacleAt(int x, int y) {
        for (Entity e : stillObjects) {
            if ((e instanceof Wall || e instanceof Brick) && x == e.getX() && y == e.getY()) {
                return true;
            }
        }
        return false;
    }

    // Kiểm tra hợp lệ khi di chuyển
    public static boolean validate(int x, int y) {
        return (1 <= x && x < WIDTH - 1 && 1 <= y && y < HEIGHT - 1 && !hasObstacleAt(x, y));
    }
}
