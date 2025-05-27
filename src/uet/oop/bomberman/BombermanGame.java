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
    public static int portalX;
    public static int portalY;

    private long lastTimer;

    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();

    private static Bomber bomberman;
    public static Portal portal;

    private AudioClip bombExplosionSound;
    private AudioClip bombPlaceSound;

    private InfoBar infoBar;

    private AnimationTimer gameTimer;

    private int currentLevel = 1;
    private final int TOTAL_LEVELS = 3;

    private static Portal levelPortal;

    private final int GAME_DURATION_SECONDS = 25;
    private int remainingTime = GAME_DURATION_SECONDS;
    private MainApp mainAppInstance;

    public BombermanGame(MainApp mainApp, Stage primaryStage) {
        this.mainAppInstance = mainApp;
        this.stage = primaryStage;
    }

    public BombermanGame(Stage stage) {
        this.stage = stage;
    }

    public BombermanGame() {
    }

    public static Bomber getBomberman() {
        return bomberman;
    }

    public uet.oop.bomberman.UI.MainApp getMainAppInstance() {
        return mainAppInstance;
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
        if (infoBar != null) {
            infoBar.setLevel(currentLevel); // Cập nhật InfoBar
        }
        // **ĐIỀU CHỈNH TẠI ĐÂY:** Di chuyển vòng lặp tìm kiếm Bomber và Portal vào một phương thức riêng
        // để tránh lặp lại và đảm bảo logic tập trung.
        findBomberAndPortal();


        if (bomberman != null) {
            bomberman.setGameInstance(this);
            bomberman.handleKeyEvent(scene);
            bomberman.setBombSounds(bombPlaceSound, bombExplosionSound);
        } else {
            System.err.println("Warning: Bomber object not found in entities after map creation.");
        }
        if (levelPortal == null) {
            System.err.println("Warning: Portal object not found in entities after map creation.");
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
                    System.out.println("FPS: + fps");
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
        // Không cần gán bomberman = null; và levelPortal = null; ở đây
        // vì chúng ta sẽ gán lại chúng trong findBomberAndPortal() ngay sau đó.

        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level" + currentLevel + ".txt");

        map = levelInfo.map;
        stillObjects = levelLoader.loadStillObjects(levelInfo);
        List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
        entities.addAll(loadedEntities);

        // Reset thời gian khi tạo map mới (bắt đầu một game mới)
        remainingTime = GAME_DURATION_SECONDS;
        if (infoBar != null) {
            infoBar.setTime(remainingTime);
            infoBar.setScore(0); // Reset score khi game mới
        }
    }

    // **PHƯƠNG THỨC MỚI ĐƯỢC THÊM VÀO ĐÂY**
    private void findBomberAndPortal() {
        bomberman = null; // Đảm bảo reset trước khi tìm kiếm
        levelPortal = null; // Đảm bảo reset trước khi tìm kiếm
        for (Entity e : entities) {
            if (e instanceof Bomber) {
                bomberman = (Bomber) e;
            } else if (e instanceof Portal) {
                levelPortal = (Portal) e;
            }
            if (bomberman != null && levelPortal != null) {
                break; // Tìm thấy cả hai, thoát vòng lặp
            }
        }
    }

    public void update() {
        List<Entity> entitiesCopy = new ArrayList<>(entities);
        for (int i = entitiesCopy.size() - 1; i >= 0; i--) {
            entitiesCopy.get(i).update();
        }

        if (bomberman != null) {
            infoBar.setScore(bomberman.getScore());
            infoBar.setLevel(this.currentLevel);
        }
        checkLevelCompletion(); // Gọi phương thức kiểm tra qua màn
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
        for (Entity e : entities) {
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
        for (Entity e : entities) {
            if (e.getX() == x && e.getY() == y) {
                result.add(e);
            }
        }
        return result;
    }

    public void gameOver(MainApp mainApp) {
        GameResultScreen gameOverScreen = new GameResultScreen(mainApp, GameResult.LOSE);
        Scene gameOverScene = new Scene(gameOverScreen);
        stage.setScene(gameOverScene);
    }

    public void continueGame() {
        System.out.println("Continuing game...");

        try {
            entities.clear();
            stillObjects.clear();
            // Không cần gán bomberman = null; và levelPortal = null; ở đây.

            LevelLoader levelLoader = new LevelLoader();
            LevelLoader.LevelInfo levelInfo = levelLoader.loadSavedLevel("res/savegame.txt");

            map = levelInfo.map;
            stillObjects = levelLoader.loadStillObjects(levelInfo);
            List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
            entities.addAll(loadedEntities);

            // **ĐIỀU CHỈNH TẠI ĐÂY:** Gọi phương thức tìm kiếm chung
            findBomberAndPortal();

            remainingTime = GAME_DURATION_SECONDS;
            if (infoBar != null) {
                infoBar.setTime(remainingTime);
                if (bomberman != null) {
                    infoBar.setScore(bomberman.getScore());
                    infoBar.setLevel(this.currentLevel);
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
        bomberman = null;
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
                            if (entity instanceof Bomber) {
                                mapChar = 'p';
                                found = true;
                                break;
                            } else if (entity instanceof Balloom) {
                                mapChar = '1';
                                found = true;
                                break;
                            } else if (entity instanceof Oneal) {
                                mapChar = '2';
                                found = true;
                                break;
                            } else if (entity instanceof Portal) { // Nếu Portal đã hiện
                                mapChar = 'x'; // Lưu lại Portal
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) { // Nếu chưa tìm thấy entity động, kiểm tra stillObjects
                        for (Entity entity : stillObjects) {
                            if (entity.getX() == j && entity.getY() == i) {
                                if (entity instanceof Wall) {
                                    mapChar = '#';
                                    found = true;
                                    break;
                                }
                                // KHÔNG LƯU BRICK TRONG STILLOBJECTS NỮA, NÓ LÀ ENTITY ĐỘNG
                                // Bricks sẽ được đọc lại khi load game mới.
                            }
                        }
                    }
                    if (!found) { // Nếu vẫn chưa tìm thấy, kiểm tra Brick
                        for (Entity entity : entities) {
                            if (entity instanceof Brick && entity.getX() == j && entity.getY() == i) {
                                mapChar = '*'; // Lưu Brick
                                found = true;
                                break;
                            }
                        }
                    }
                    line.append(mapChar);
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
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

        if (mainApp != null) {
            mainApp.showGameResult(result);
        } else {
            showGameResult(null, result);
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

        if (hasObstacleAt(tileLeft, tileTop) || hasDestructibleAt(tileLeft, tileTop)
        || hasBlockingBombAt(tileLeft, tileTop)) return false;
        if (hasObstacleAt(tileRight, tileTop) || hasDestructibleAt(tileRight, tileTop)
        || hasBlockingBombAt(tileRight, tileTop)) return false;
        if (hasObstacleAt(tileLeft, tileBottom) || hasDestructibleAt(tileLeft, tileBottom)
        || hasBlockingBombAt(tileRight, tileTop)) return false;
        if (hasObstacleAt(tileRight, tileBottom) || hasDestructibleAt(tileRight, tileBottom)
        || hasBlockingBombAt(tileRight, tileBottom)) return false;
        return true;
    }

    private static boolean hasBlockingBombAt(int tileX, int tileY) {
        for (Entity e : getEntitiesAt(tileX, tileY)) {
            if (e instanceof Bomb) {
                Bomb b = (Bomb) e;
                if (!b.isExploded() && !b.isCanWalkThrough()) return true;
            }
        }
        return false;
    }

    public static boolean hasEnemyAt(int tileX, int tileY) {
        for (Entity e : getEntitiesAt(tileX, tileY)) {
            if (e instanceof Enemy && !((Enemy) e).isDead()) {
                return true;
            }
        }
        return false;
    }

    private void checkLevelCompletion() {
        if (bomberman == null || levelPortal == null) {
            return; // Không làm gì nếu bomberman hoặc portal chưa được tạo
        }

        // 1. Kiểm tra tất cả quái vật đã bị tiêu diệt
        boolean allEnemiesDead = true;
        for (Entity e : entities) {
            if (e instanceof Enemy) {
                allEnemiesDead = false;
                break;
            }
        }

        // 2. Nếu tất cả quái vật đã chết, kiểm tra và hiện Portal
        if (allEnemiesDead) {
            // Kiểm tra xem có Brick nào đang che Portal không
            boolean isBrickAtPortalLocation = false;
            for (Entity e : entities) { // Duyệt qua entities (nơi Brick đang nằm)
                if (e instanceof Brick && e.getX() == levelPortal.getX() && e.getY() == levelPortal.getY()) {
                    isBrickAtPortalLocation = true;
                    break;
                }
            }

            // Nếu không có Brick nào ở vị trí Portal và Portal đang bị ẩn, thì hiện Portal
            if (!isBrickAtPortalLocation && levelPortal.isHidden()) {
                levelPortal.setHidden(false);
                System.out.println("Portal is now visible!");
            }

            // 3. Nếu Portal đã hiện và Bomber đứng trên đó
            if (!levelPortal.isHidden() && bomberman.getX() == levelPortal.getX() && bomberman.getY() == levelPortal.getY()) {
                System.out.println("Level " + currentLevel + " completed!");
                currentLevel++;
                if (currentLevel <= TOTAL_LEVELS) {
                    System.out.println("Moving to Level " + currentLevel);
                    loadNextLevel();
                } else {
                    System.out.println("All levels completed! You Win!");
                    if (gameTimer != null) {
                        gameTimer.stop();
                    }
                    if (mainAppInstance != null) {
                        mainAppInstance.showGameResult(GameResult.WIN);
                    }
                }
                return;
            }
        }
    }

    private void loadNextLevel() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        if (mainAppInstance != null) {
            mainAppInstance.stopBackgroundMusic();
            mainAppInstance.playBackgroundMusic("res/sounds/game_music.mp3");
        }

        remainingTime = GAME_DURATION_SECONDS;
        if (infoBar != null) {
            infoBar.setTime(remainingTime);
        }

        entities.clear();
        stillObjects.clear();
        // Không cần gán bomberman = null; và levelPortal = null; ở đây.

        try {
            LevelLoader levelLoader = new LevelLoader();
            LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level" + currentLevel + ".txt");

            map = levelInfo.map;
            stillObjects = levelLoader.loadStillObjects(levelInfo);
            List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
            entities.addAll(loadedEntities);

            // **ĐIỀU CHỈNH TẠI ĐÂY:** Gọi phương thức tìm kiếm chung
            findBomberAndPortal();

            if (bomberman != null && stage != null && stage.getScene() != null) {
                Scene currentScene = stage.getScene();
                bomberman.handleKeyEvent(currentScene);
                bomberman.setBombSounds(bombPlaceSound, bombExplosionSound);
                infoBar.setLevel(currentLevel);
            } else {
                System.err.println("Warning: Bomber object not found after loading new level.");
                if (mainAppInstance != null) {
                    mainAppInstance.showGameResult(GameResult.LOSE);
                }
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
            System.err.println("Error loading next level: " + e.getMessage());
            if (mainAppInstance != null) {
                mainAppInstance.showGameResult(GameResult.LOSE);
            }
        }
    }

    public static boolean hasFlameAt(int tileX, int tileY) {
        for (Entity e : entities) {
            // Giả định lớp Flame có phương thức isFinished() để kiểm tra xem flame đã tắt chưa
            if (e instanceof Flame && e.getX() == tileX && e.getY() == tileY && !((Flame) e).isFinished()) {
                return true;
            }
        }
        return false;
    }
}