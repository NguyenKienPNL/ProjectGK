package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import uet.oop.bomberman.engine.LevelLoader;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.Items.BombItem; // Đảm bảo import này nếu bạn lưu BombItem
import uet.oop.bomberman.entities.Items.FlameItem;
import uet.oop.bomberman.entities.Items.SpeedItem;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.UI.MainApp;
import java.io.BufferedWriter;
import java.io.FileWriter;
import uet.oop.bomberman.UI.GameResult;
import uet.oop.bomberman.UI.InfoBar;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;


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

    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();

    private static Bomber bomberman;

    private InfoBar infoBar;
    private int timeLeft = 200; // Thời gian khởi tạo ban đầu, có thể đổi tùy ý
    private MainApp mainApp;
    private AnimationTimer gameLoop;
    private int score = 0;
    private int currentLevel = 1;


    // Constructor mới để nhận cả MainApp và Stage
    public BombermanGame(MainApp mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.infoBar = new InfoBar();
    }

    public BombermanGame() {} // Giữ lại constructor mặc định nếu cần


    public static Bomber getBomberman() {
        return bomberman;
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (this.stage == null) {
            this.stage = stage;
        }
        if (this.infoBar == null) {
            this.infoBar = new InfoBar();
        }

        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();
        VBox gameContent = new VBox();
        gameContent.getChildren().addAll(infoBar, canvas);

        StackPane root = new StackPane();
        root.getChildren().add(gameContent);

        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.show();

        lastTimer = System.currentTimeMillis();

        infoBar.getPauseButton().setOnAction(e -> pauseGame());

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();

                frames++;
                if (now - lastTimer >= 1_000_000_000) {
                    fps = frames;
                    frames = 0;
                    lastTimer = now;

                    if (BombermanGame.this.stage != null) {
                        BombermanGame.this.stage.setTitle("Bomberman FPS: " + fps);
                    }

                    // Trừ thời gian mỗi giây
                    timeLeft--;
                    if (timeLeft <= 0) {
                        // Hết giờ => Thua
                        endGame(mainApp, GameResult.LOSE);
                        return;
                    }

                    infoBar.setTime(timeLeft);
                    infoBar.setScore(score);
                    infoBar.setLevel(currentLevel);
                }
            }
        };
        gameLoop.start();
    }

    public void createMap() throws IOException {
        entities.clear();
        stillObjects.clear();

        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level" + currentLevel + ".txt");

        map = levelInfo.map;

        stillObjects = levelLoader.loadStillObjects(levelInfo);
        List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
        entities.addAll(loadedEntities);

        for (Entity e : entities) {
            if (e instanceof Bomber) {
                bomberman = (Bomber) e;
                break;
            }
        }
        if (bomberman != null && stage != null && stage.getScene() != null) {
            bomberman.handleKeyEvent(stage.getScene());
        } else {
            System.err.println("Warning: Could not attach key event handler to Bomber. Bomber or Scene is null.");
        }
    }

    public void update() {
        for (Entity entity : entities) {
            entity.update();
        }

        // Kiểm tra điều kiện thắng game: Chỉ khi tất cả kẻ thù bị tiêu diệt
        if (checkWinCondition()) {
            endGame(mainApp, GameResult.WIN);
        }
    }

    // Hàm kiểm tra điều kiện thắng game (không còn kẻ thù)
    private boolean checkWinCondition() {
        for (Entity e : entities) {
            if (e instanceof Enemy) { // Chỉ cần tìm thấy một kẻ thù là chưa thắng
                return false;
            }
        }
        return true; // Không còn kẻ thù nào
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
            if (e instanceof Brick && x == e.getX() && e.getY() == y && !((Brick) e).isDestroyed()) {
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

    // Hàm Game Over (sẽ gọi endGame chính)
    public void gameOver(MainApp mainApp) {
        endGame(mainApp, GameResult.LOSE);
    }


    public void continueGame() {
        System.out.println("Continuing game...");

        try {
            LevelLoader levelLoader = new LevelLoader();
            LevelLoader.LevelInfo levelInfo = levelLoader.loadSavedLevel("res/savegame.txt");

            entities.clear();
            stillObjects.clear();

            stillObjects = levelLoader.loadStillObjects(levelInfo);
            List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
            entities.addAll(loadedEntities);

            for (Entity e : entities) {
                if (e instanceof Bomber) {
                    bomberman = (Bomber) e;
                    break;
                }
            }

            if (bomberman != null && stage != null && stage.getScene() != null) {
                bomberman.handleKeyEvent(stage.getScene());
            } else {
                System.err.println("Bomberman not found after loading saved game or Scene is null. Returning to Start Menu.");
                if (mainApp != null) {
                    mainApp.showStartMenu();
                }
                return;
            }

            if (gameLoop == null) {
                System.err.println("gameLoop was null in continueGame, re-initializing. This might indicate an issue with game startup.");
                start(this.stage);
            } else {
                gameLoop.start();
            }

            this.timeLeft = levelInfo.timeLeft;
            this.score = levelInfo.score;
            this.currentLevel = levelInfo.level;
            infoBar.setTime(this.timeLeft);
            infoBar.setScore(this.score);
            infoBar.setLevel(this.currentLevel);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading saved game. Returning to Start Menu.");
            if (mainApp != null) {
                mainApp.showStartMenu();
            }
        }
    }

    public void saveGame(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 1. Lưu Level, Width, Height
            writer.write(bomberman.getCurrentLevel() + " " + WIDTH + " " + HEIGHT);
            writer.newLine();

            // 2. Lưu thời gian và điểm
            writer.write(timeLeft + " " + score);
            writer.newLine();

            // 3. Lưu trạng thái buff (nếu cần)
            writer.write(bomberman.getBombCount() + " " + bomberman.getBombRadius() + " " + bomberman.getSpeed());
            writer.newLine();

            // 4. Lưu thông tin bản đồ theo từng dòng
            for (int i = 0; i < HEIGHT; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < WIDTH; j++) {
                    char mapChar = ' ';

                    // Ưu tiên Bomber và Enemy (đảm bảo Enemy cũng được lưu lại đúng ký tự)
                    for (Entity e : entities) {
                        if (e.getX() == j && e.getY() == i) {
                            if (e instanceof Bomber) {
                                mapChar = 'p';
                                break;
                            } else if (e instanceof Balloom) {
                                mapChar = '1'; // Ký tự cho Balloom
                                break;
                            } else if (e instanceof Oneal) {
                                mapChar = '2'; // Ký tự cho Oneal
                                break;
                            }
                            // Thêm các loại enemy khác nếu có, ví dụ:
                            // else if (e instanceof Doll) { mapChar = '3'; break; }
                            // else if (e instanceof Minvo) { mapChar = '4'; break; }
                            // else if (e instanceof Kondoria) { mapChar = '5'; break; }
                        }
                    }

                    // Nếu không có entity động (Bomber/Enemy), kiểm tra stillObjects
                    if (mapChar == ' ') {
                        for (Entity e : stillObjects) {
                            if (e.getX() == j && e.getY() == i) {
                                if (e instanceof Wall) mapChar = '#';
                                else if (e instanceof Brick) mapChar = '*';
                                else if (e instanceof Grass) mapChar = ' ';
                                    // Đã loại bỏ Portal: else if (e instanceof Portal) mapChar = 'x';
                                else if (e instanceof BombItem) mapChar = 'b';
                                else if (e instanceof FlameItem) mapChar = 'f';
                                else if (e instanceof SpeedItem) mapChar = 's';
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


    // Hàm kết thúc game (khi thắng hoặc thua)
    public void endGame(MainApp mainApp, GameResult result) {
        // Dừng game loop của BombermanGame ngay lập tức
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Lưu game trước khi kết thúc (tùy chọn: có thể không lưu nếu thua)
        try {
            saveGame("res/savegame.txt");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving game.");
        }

        // Gọi phương thức showGameResult của MainApp để xử lý hiển thị màn hình và nhạc
        mainApp.showGameResult(result);
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

    private void pauseGame() {
        System.out.println("Pause button clicked. Attempting to pause game.");

        if (gameLoop != null) {
            gameLoop.stop();
            System.out.println("Game loop stopped.");
        }

        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        pauseMenu.prefWidthProperty().bind(stage.getScene().widthProperty());
        pauseMenu.prefHeightProperty().bind(stage.getScene().heightProperty());
        System.out.println("Pause menu created with size: " + canvas.getWidth() + "x" + canvas.getHeight());


        Button continueBtn = new Button("Continue");
        Button saveExitBtn = new Button("Save & Exit");

        String buttonStyle = "-fx-font-size: 20px; " +
                "-fx-font-family: 'Courier New'; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: #444; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 5; " +
                "-fx-border-radius: 5;";
        continueBtn.setStyle(buttonStyle);
        saveExitBtn.setStyle(buttonStyle);

        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(buttonStyle + "-fx-background-color: #666;"));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle(buttonStyle));
        saveExitBtn.setOnMouseEntered(e -> saveExitBtn.setStyle(buttonStyle + "-fx-background-color: #666;"));
        saveExitBtn.setOnMouseExited(e -> saveExitBtn.setStyle(buttonStyle));


        pauseMenu.getChildren().addAll(continueBtn, saveExitBtn);

        StackPane root = (StackPane) stage.getScene().getRoot();
        if (root == null) {
            System.err.println("Error: Root StackPane is null. Cannot add pause menu.");
            return;
        }
        System.out.println("Root StackPane found. Adding pause menu to root.");


        root.getChildren().add(pauseMenu);
        System.out.println("Pause menu added to root. Children count: " + root.getChildren().size());


        continueBtn.setOnAction(e -> {
            System.out.println("Continue button clicked.");
            root.getChildren().remove(pauseMenu);

            if (gameLoop != null) {
                gameLoop.start();
                System.out.println("Game loop started.");
            }
        });

        saveExitBtn.setOnAction(e -> {
            System.out.println("Save & Exit button clicked.");
            try {
                saveGame("res/savegame.txt");
                System.out.println("Game saved.");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Error saving game: " + ex.getMessage());
            }

            mainApp.showStartMenu();

            root.getChildren().remove(pauseMenu);
            System.out.println("Pause menu removed and returning to Start Menu.");
        });
    }
}