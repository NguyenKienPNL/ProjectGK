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
import uet.oop.bomberman.UI.InfoBar;
import javafx.scene.layout.BorderPane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import uet.oop.bomberman.UI.GameResult;
import javafx.scene.media.AudioClip;

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

    // GIỮ CÁC DANH SÁCH NÀY LÀ STATIC
    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();

    private static Bomber bomberman;

    private AudioClip bombExplosionSound;
    private AudioClip bombPlaceSound;

    private InfoBar infoBar;

    private AnimationTimer gameTimer;

    private final int GAME_DURATION_SECONDS = 15;
    private int remainingTime = GAME_DURATION_SECONDS;
    private MainApp mainAppInstance;

    public BombermanGame(MainApp mainApp, Stage primaryStage) {
        this.mainAppInstance = mainApp;
        this.stage = primaryStage;
    }

    public BombermanGame(Stage stage) {
        this.stage = stage;
    }

    public BombermanGame() {}

    public static Bomber getBomberman() {
        return bomberman;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        BorderPane root = new BorderPane();

        infoBar = new InfoBar();
        root.setTop(infoBar);
        root.setCenter(canvas);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        infoBar.getPauseButton().setOnAction(e -> {
            System.out.println("Nút tạm dừng đã được nhấn!");
            // TODO: Thêm logic tạm dừng/tiếp tục game ở đây
        });

        try {
            String explosionSoundPath = getClass().getResource("/sounds/explosion.mp3").toExternalForm();
            bombExplosionSound = new AudioClip(explosionSoundPath);
            System.out.println("Loaded explosion sound: " + explosionSoundPath);
        } catch (NullPointerException e) {
            System.err.println("Error loading explosion sound: " + e.getMessage());
            System.err.println("Make sure '/sounds/explosion.mp3' exists and is correctly placed in your resources folder.");
            bombExplosionSound = null;
        }

        try {
            String placeSoundPath = getClass().getResource("/sounds/tick.mp3").toExternalForm();
            bombPlaceSound = new AudioClip(placeSoundPath);
            System.out.println("Loaded bomb place sound: " + placeSoundPath);
        } catch (NullPointerException e) {
            System.err.println("Error loading bomb place sound: " + e.getMessage());
            System.err.println("Make sure '/sounds/tick.mp3' exists and is correctly placed in your resources folder.");
            bombPlaceSound = null;
        }

        createMap(); // Gọi createMap để tải map và các entities

        // Tìm Bomber sau khi map đã được tạo và entities đã được load
        for (Entity e : entities) {
            if (e instanceof Bomber) {
                bomberman = (Bomber) e;
                break;
            }
        }

        if (bomberman != null) {
            bomberman.handleKeyEvent(scene);
            bomberman.setBombSounds(bombPlaceSound, bombExplosionSound);
        } else {
            System.err.println("Warning: Bomber object not found in entities after map creation.");
        }


        lastTimer = System.currentTimeMillis();
        gameTimer = new AnimationTimer() {
            private long lastSecondTime = 0;

            @Override
            public void handle(long now) {
                update();
                render();

                frames++;

                if (now - lastTimer >= 1_000_000_000) {
                    fps = frames;
                    System.out.println("FPS: " + fps);
                    frames = 0;
                    lastTimer = now;
                    stage.setTitle("Bomberman FPS: " + fps);

                    remainingTime--;
                    infoBar.setTime(remainingTime);

                    if (remainingTime <= 0) {
                        System.out.println("Time is up! Game Over.");
                        gameTimer.stop();
                        if (mainAppInstance != null) {
                            mainAppInstance.showGameResult(GameResult.LOSE);
                        } else {
                            gameOver(null);
                        }
                    }
                }
            }
        };
        gameTimer.start();
    }

    public void createMap() throws IOException {
        // THÊM DÒNG NÀY: Xóa các thực thể cũ trước khi tạo map mới
        entities.clear();
        stillObjects.clear();
        bomberman = null; // Đảm bảo bomberman được reset

        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level2.txt");

        map = levelInfo.map;
        stillObjects = levelLoader.loadStillObjects(levelInfo);
        List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
        entities.addAll(loadedEntities);

        // Reset thời gian khi tạo map mới (bắt đầu một game mới)
        remainingTime = GAME_DURATION_SECONDS;
        if (infoBar != null) {
            infoBar.setTime(remainingTime);
            infoBar.setScore(0); // Reset score khi game mới
            infoBar.setLevel(1); // Reset level khi game mới
        }
    }

    public void update() {
        // Cần copy danh sách entities để tránh lỗi ConcurrentModificationException
        // nếu có entities bị xóa trong quá trình update (ví dụ: enemy chết)
        List<Entity> entitiesCopy = new ArrayList<>(entities);
        for (int i = entitiesCopy.size() - 1; i >= 0; i--) {
            entitiesCopy.get(i).update();
        }
        // entities có thể đã thay đổi, không cần gán lại entitiesCopy vào entities

        if (bomberman != null) {
            infoBar.setScore(bomberman.getScore());
            infoBar.setLevel(bomberman.getCurrentLevel());
        }
    }

    public static void addEntity(Entity e) {
        entities.add(e);
    }

    public static void removeEntity(Entity e) {
        entities.remove(e);
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Entity entity : stillObjects) {
            entity.render(gc);
        }

        for (Entity entity : entities) {
            entity.render(gc);
        }
    }

    public static boolean hasObstacleAt(int x, int y) {
        for (Entity e : stillObjects) {
            if (e instanceof Wall && x == e.getX() && y == e.getY()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasDestructibleAt(int x, int y) {
        for (Entity e : entities) {
            if (e instanceof Brick && x == e.getX() && y == e.getY() && !((Brick) e).isDestroyed()) {
                return true;
            }
        }
        return false;
    }

    public static boolean validate(int x, int y) {
        return (1 <= x && x < WIDTH - 1 && 1 <= y && y < HEIGHT - 1 &&
                !hasObstacleAt(x, y) && !hasDestructibleAt(x, y));
    }

    public static boolean hasPlayerOrEnemyAt(int x, int y) {
        for(Entity e : entities) {
            if ((e instanceof Bomber || e instanceof Enemy) && x == e.getX() && e.getY() == y) {
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
            // Kiểm tra theo tọa độ ô, không phải pixel
            if (e.getX() == x && e.getY() == y) {
                result.add(e);
            }
            // Logic thêm Bomb có thể phức tạp hơn nếu bomb không chiếm trọn 1 ô
            // Hiện tại, e.getX() và e.getY() của Bomb cũng là tọa độ ô
        }
        return result;
    }

    public void gameOver(MainApp mainApp) {
        // Phương thức này hiện tại không còn được gọi trực tiếp khi hết giờ,
        // nhưng vẫn có thể dùng cho các trường hợp game over khác (ví dụ: bomber chết)
        GameResultScreen gameOverScreen = new GameResultScreen(mainApp, GameResult.LOSE);
        Scene gameOverScene = new Scene(gameOverScreen);
        stage.setScene(gameOverScene);
    }

    public void continueGame() {
        System.out.println("Continuing game...");

        try {
            // Đảm bảo clear trước khi load game đã lưu
            entities.clear();
            stillObjects.clear();
            bomberman = null; // Đảm bảo bomberman được reset

            LevelLoader levelLoader = new LevelLoader();
            LevelLoader.LevelInfo levelInfo = levelLoader.loadSavedLevel("res/savegame.txt");

            // Load dữ liệu game đã lưu
            map = levelInfo.map;
            stillObjects = levelLoader.loadStillObjects(levelInfo);
            List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
            entities.addAll(loadedEntities);

            // Tìm bomberman mới sau khi load
            for (Entity e : entities) {
                if (e instanceof Bomber) {
                    bomberman = (Bomber) e;
                    break;
                }
            }

            // Reset thời gian và cập nhật InfoBar dựa trên trạng thái game đã lưu (nếu có)
            remainingTime = GAME_DURATION_SECONDS; // Hoặc load từ savegame nếu bạn lưu thời gian
            if (infoBar != null) {
                infoBar.setTime(remainingTime);
                if (bomberman != null) {
                    infoBar.setScore(bomberman.getScore());
                    infoBar.setLevel(bomberman.getCurrentLevel());
                }
            }


            if (bomberman != null && stage != null && stage.getScene() != null) {
                Scene currentScene = stage.getScene();
                bomberman.handleKeyEvent(currentScene);
                bomberman.setBombSounds(bombPlaceSound, bombExplosionSound);
            } else {
                System.err.println("Bomberman not found after loading saved game.");
            }

            if (gameTimer != null) {
                gameTimer.stop();
            }

            gameTimer = new AnimationTimer() {
                private long lastSecondTime = 0;
                @Override
                public void handle(long now) {
                    update();
                    render();

                    if (now - lastSecondTime >= 1_000_000_000) {
                        remainingTime--;
                        infoBar.setTime(remainingTime);
                        lastSecondTime = now;

                        if (remainingTime <= 0) {
                            System.out.println("Time is up! Game Over.");
                            gameTimer.stop();
                            if (mainAppInstance != null) {
                                mainAppInstance.showGameResult(GameResult.LOSE);
                            } else {
                                gameOver(null);
                            }
                        }
                    }
                    if (bomberman != null) {
                        infoBar.setScore(bomberman.getScore());
                        infoBar.setLevel(bomberman.getCurrentLevel());
                    }
                }
            };
            gameTimer.start();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading saved game.");
            // Quay về menu nếu load game lỗi
            if (mainAppInstance != null) {
                mainAppInstance.showStartMenu();
            }
        }
    }

    public void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (bombExplosionSound != null) {
            bombExplosionSound.stop();
            bombExplosionSound = null;
        }
        if (bombPlaceSound != null) {
            bombPlaceSound.stop();
            bombPlaceSound = null;
        }
        // KHÔNG CẦN CLEAR CÁC LIST STATIC Ở ĐÂY NỮA
        // Chúng sẽ được clear ở createMap() hoặc continueGame()
        bomberman = null; // Đảm bảo bomberman được reset
        System.out.println("BombermanGame instance stopped.");
    }

    public void saveGame(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(bomberman.getCurrentLevel() + " " + HEIGHT + " " + WIDTH);
            writer.newLine();

            for (int i = 0; i < HEIGHT; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < WIDTH; j++) {
                    char mapChar = ' ';
                    boolean found = false;

                    for (Entity entity : entities) {
                        if (entity.getX() == j && entity.getY() == i) {
                            if (entity instanceof Bomber) { mapChar = 'p'; found = true; break; }
                            else if (entity instanceof Balloom) { mapChar = '1'; found = true; break; }
                            else if (entity instanceof Oneal) { mapChar = '2'; found = true; break; }
                        }
                    }

                    if (!found) {
                        for (Entity entity : stillObjects) {
                            if (entity.getX() == j && entity.getY() == i) {
                                if (entity instanceof Wall) { mapChar = '#'; found = true; break; }
                                else if (entity instanceof Brick) { mapChar = '*'; found = true; break; }
                            }
                        }
                    }
                    line.append(mapChar);
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endGame(MainApp mainApp, GameResult result) {
        try {
            saveGame("res/savegame.txt");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving game.");
        }

        // Gọi mainApp để xử lý kết quả game, bao gồm đổi nhạc
        if (mainApp != null) {
            mainApp.showGameResult(result);
        } else {
            showGameResult(null, result); // Fallback nếu mainApp null
        }
    }

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

        if (hasObstacleAt(tileLeft, tileTop) || hasDestructibleAt(tileLeft, tileTop)) return false;
        if (hasObstacleAt(tileRight, tileTop) || hasDestructibleAt(tileRight, tileTop)) return false;
        if (hasObstacleAt(tileLeft, tileBottom) || hasDestructibleAt(tileLeft, tileBottom)) return false;
        if (hasObstacleAt(tileRight, tileBottom) || hasDestructibleAt(tileRight, tileBottom)) return false;

        for (Entity e : getEntitiesAt(tileLeft, tileTop)) {
            if (e instanceof Bomb && !((Bomb)e).isExploded()) return false;
        }
        for (Entity e : getEntitiesAt(tileRight, tileTop)) {
            if (e instanceof Bomb && !((Bomb)e).isExploded()) return false;
        }
        for (Entity e : getEntitiesAt(tileLeft, tileBottom)) {
            if (e instanceof Bomb && !((Bomb)e).isExploded()) return false;
        }
        for (Entity e : getEntitiesAt(tileRight, tileBottom)) {
            if (e instanceof Bomb && !((Bomb)e).isExploded()) return false;
        }

        return true;
    }
}