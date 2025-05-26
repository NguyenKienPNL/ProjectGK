package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.UI.GameResultScreen;
import uet.oop.bomberman.engine.LevelLoader;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.UI.MainApp;
import java.io.BufferedWriter;
import java.io.FileWriter;
import uet.oop.bomberman.UI.GameResult;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BombermanGame extends Application {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 15;

    public char[][] map;

    private GraphicsContext gc;
    private Canvas canvas;
    private Stage stage;

    public int fps = 0;
    public int frames = 0;
    private long lastTimer;

    // List động và tĩnh tách riêng
    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();

    // Bomber tham chiếu riêng nếu cần dùng trực tiếp
    private static Bomber bomberman;

    
    public BombermanGame(MainApp mainApp, Stage primaryStage) {}
    public BombermanGame(Stage stage) {
        this.stage = stage;
    }

    public static Bomber getBomberman() {
        return bomberman;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;  // Cập nhật stage khi khởi tạo

        // Canvas setup
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

        lastTimer = (int)System.currentTimeMillis();
        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();

                frames++;

                // Cứ mỗi giây tính lại fps
                if (now - lastTimer >= 1000000000) {
                    fps = frames;
//                    System.out.println("FPS: " + fps);
                    frames = 0;
                    lastTimer = now;
                    stage.setTitle("Bomberman FPS: " + fps);
                }
            }
        };
        timer.start();
    }

    public void createMap() throws IOException {
        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level2.txt");

        // Lấy map
        map = levelInfo.map;

        // Load object tĩnh
        stillObjects = levelLoader.loadStillObjects(levelInfo);

        // Load các entity động
        List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
        entities.addAll(loadedEntities);
    }

    public void update() {
        // Update toàn bộ các entity động
        for (int i = entities.size() - 1; i >= 0; i--) {
            entities.get(i).update();
        }
    }

    public static void addEntity(Entity e) {
        entities.add(e);
    }

    public static void removeFlame(Entity e) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) instanceof FlameSegments && entities.get(i).equals(e)) {
                entities.remove(i);
                break;
            }
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

    // Hàm kiểm tra vật cản không phá được (Wall)
    public static boolean hasObstacleAt(int x, int y) {
        for (Entity e : stillObjects) {
            if (e instanceof Wall && x == e.getX() && y == e.getY()) {
                return true;
            }
        }
        return false;
    }

    // Hàm kiểm tra vật cản phá được (Brick)
    public static boolean hasDestructibleAt(int x, int y) {
        for (Entity e : entities) {
            if (e instanceof Brick && x == e.getX() && y == e.getY() && !((Brick) e).isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    // Kiểm tra hợp lệ khi di chuyển (tile-based)
    public static boolean validate(int x, int y) {
        return (1 <= x && x < WIDTH - 1 && 1 <= y && y < HEIGHT - 1 &&
                !hasObstacleAt(x, y) && !hasDestructibleAt(x, y));
    }

    public static boolean hasPlayerOrEnemyAt(int x, int y) {
        for(Entity e : entities) {
            if ((e instanceof Bomber || e instanceof Enemy) && x == e.getX() && y == e.getY()) {
                return true;
            }
        }
        return false;
    }

    public static Entity getStillObjectAt(int x, int y) {
        for (Entity e : stillObjects) {
            if (e.getX() == x && e.getY() == y) {
                return e;
            }
        }
        return null;
    }

    public static List<Entity> getEntitiesAt(int x, int y) {
        List<Entity> result = new ArrayList<>();
        for (Entity e: entities) {
            if (e.getX() == x && e.getY() == y) {
                result.add(e);
            }
        }
        return result;
    }

    // Hàm Game Over
    public void gameOver(MainApp mainApp) {
        GameResultScreen gameOverScreen = new GameResultScreen(mainApp, GameResult.LOSE);
        Scene gameOverScene = new Scene(gameOverScreen);
        stage.setScene(gameOverScene);
    }


    public void continueGame() {
        System.out.println("Continuing game...");

        try {
            // Tạo LevelLoader để tải lại thông tin từ file lưu
            LevelLoader levelLoader = new LevelLoader();

            // Tải cấp độ đã lưu
            LevelLoader.LevelInfo levelInfo = levelLoader.loadSavedLevel("res/savegame.txt");

            // Xóa các entity và object cũ
            entities.clear();
            stillObjects.clear();

            // Load lại các đối tượng tĩnh và động từ cấp độ đã lưu
            stillObjects = levelLoader.loadStillObjects(levelInfo);
            List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
            entities.addAll(loadedEntities);

            // Tìm Bomber trong danh sách entities
            for (Entity e : entities) {
                if (e instanceof Bomber) {
                    bomberman = (Bomber) e;
                    break;
                }
            }

            // Cập nhật các sự kiện từ bàn phím cho Bomber
            Scene scene = stage.getScene();
            bomberman.handleKeyEvent(scene);

            // Tiếp tục vòng lặp game
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    update(); // Cập nhật trạng thái game
                    render(); // Vẽ lại màn hình
                }
            };
            timer.start(); // Bắt đầu vòng lặp game

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading saved game.");
        }
    }

    public void saveGame(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        // Lưu thông tin cấp độ, hàng và cột
        writer.write(bomberman.getCurrentLevel() + " " + HEIGHT + " " + WIDTH); // Ví dụ lưu cấp độ hiện tại và kích thước map
        writer.newLine();

        // Lưu bản đồ (map) từ trạng thái của các đối tượng trong stillObjects và entities
        for (int i = 0; i < HEIGHT; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < WIDTH; j++) {
                char mapChar = ' '; // Mặc định là Grass
                // Kiểm tra các đối tượng tĩnh
                for (Entity entity : stillObjects) {
                    if (entity.getX() == j && entity.getY() == i) {
                        if (entity instanceof Wall) mapChar = '#';
                        if (entity instanceof Brick) mapChar = '*';
                        break;
                    }
                }
                // Kiểm tra các đối tượng động
                for (Entity entity : entities) {
                    if (entity.getX() == j && entity.getY() == i) {
                        if (entity instanceof Bomber) mapChar = 'p'; // 'p' cho Bomber
                        if (entity instanceof Balloom) mapChar = '1'; // '1' cho Balloom
                        if (entity instanceof Oneal) mapChar = '2'; // '2' cho Oneal
                        break;
                    }
                }
                line.append(mapChar);
            }
            writer.write(line.toString());
            writer.newLine();
        }

        writer.close();
    }

    // Hàm kết thúc game (khi thắng hoặc thua)
    public void endGame(MainApp mainApp, GameResult result) {
        // Lưu game trước khi kết thúc
        try {
            saveGame("res/savegame.txt"); // Lưu game vào file savegame.txt
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving game.");
        }

        // Hiển thị màn hình kết quả
        showGameResult(mainApp, result);
    }

    // Hàm hiển thị màn hình kết quả
    public void showGameResult(MainApp mainApp, GameResult result) {
        GameResultScreen resultScreen = new GameResultScreen(mainApp, result);
        Scene resultScene = new Scene(resultScreen);
        stage.setScene(resultScene);
    }

    public static boolean validatePixelMove(int realX, int realY) {
        int tileLeft = realX / Sprite.SCALED_SIZE;
        int tileRight = (realX + Sprite.SCALED_SIZE - 1) / Sprite.SCALED_SIZE;
        int tileTop = realY / Sprite.SCALED_SIZE;
        int tileBottom = (realY + Sprite.SCALED_SIZE - 1) / Sprite.SCALED_SIZE;

        return validate(tileLeft, tileTop)
                && validate(tileRight, tileTop)
                && validate(tileLeft, tileBottom)
                && validate(tileRight, tileBottom);
    }

}
