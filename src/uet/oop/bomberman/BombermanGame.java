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
import uet.oop.bomberman.UI.InfoBar;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;



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

    //Biến hiển thị thời gian, điểm số, màn chơi
    private InfoBar infoBar;
    private Timeline timer;
    private int timeLeft = 15;
    private MainApp mainApp;  // truyền từ MainApp
    private AnimationTimer animationTimer;
    private int score = 0;
    private int currentLevel = 1;
    private int timeRemaining = 200; // hoặc thời gian khởi tạo ban đầu tùy game của bạn
    private int currentScore = 0;    // điểm hiện tại



    public BombermanGame() {}
    public BombermanGame(Stage stage) {
        this.stage = stage;
    }

    public static Bomber getBomberman() {
        return bomberman;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.infoBar = new InfoBar();


        // Canvas game
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // ✅ Tạo InfoBar
        this.infoBar = new InfoBar();

        // ✅ Layout tổng: VBox gồm InfoBar (trên) và Canvas (dưới)
        VBox root = new VBox();
        root.getChildren().addAll(infoBar, canvas);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Load map và entity
        createMap();

        // Tìm Bomber
        for (Entity e : entities) {
            if (e instanceof Bomber) {
                bomberman = (Bomber) e;
            }
        }
        bomberman.handleKeyEvent(scene);

        lastTimer = (int) System.currentTimeMillis();

        gameLoop = new AnimationTimer() {
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

                    //  Trừ thời gian mỗi giây
                    timeLeft--;
                    if (timeLeft <= 0) {
                        // ❌ Hết giờ => Thua
                        endGame(mainApp, GameResult.LOSE);
                        stop(); // Dừng AnimationTimer
                        return;
                    }

                    //  Cập nhật InfoBar (ví dụ: giả lập dữ liệu)
                    infoBar.setTime(timeLeft);
                    infoBar.setScore(score);  // Bạn phải có biến score
                    infoBar.setLevel(currentLevel);  // Bạn cần biến currentLevel
                    infoBar.getPauseButton().setOnAction(e -> pauseGame());

                }
            }
        };
        gameLoop.start();
    }


    public void createMap() throws IOException {
        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level1.txt");

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
        for (Entity entity : entities) {
            entity.update();
        }
    }

    public static void addEntity(Entity e) {
        entities.add(e);
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
        for (Entity e : stillObjects) {
            if (e instanceof Brick && x == e.getX() && y == e.getY()) {
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
            this.timeLeft = levelInfo.timeLeft;
            this.score = levelInfo.score;
            this.currentLevel = levelInfo.level;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading saved game.");
        }


    }

    public void saveGame(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 1. Lưu Level, Width, Height
            writer.write(bomberman.getCurrentLevel() + " " + WIDTH + " " + HEIGHT);
            writer.newLine();

            // 2. Lưu thời gian và điểm
            writer.write(timeRemaining + " " + score);
            writer.newLine();

            // 3. Lưu trạng thái buff (nếu cần)
            writer.write(bomberman.getBombCount() + " " + bomberman.getBombRadius() + " " + bomberman.getSpeed());
            writer.newLine();

            // 4. Lưu thông tin bản đồ theo từng dòng
            for (int i = 0; i < HEIGHT; i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < WIDTH; j++) {
                    char mapChar = ' '; // Grass mặc định

                    // Ưu tiên Bomber và Enemy
                    for (Entity e : entities) {
                        if (e.getX() == j && e.getY() == i) {
                            if (e instanceof Bomber) {
                                mapChar = 'p';
                                break;
                            } else if (e instanceof Balloom) {
                                mapChar = '1';
                                break;
                            } else if (e instanceof Oneal) {
                                mapChar = '2';
                                break;
                            }
                        }
                    }

                    // Nếu không có enemy hay bomber, kiểm tra stillObjects
                    if (mapChar == ' ') {
                        for (Entity e : stillObjects) {
                            if (e.getX() == j && e.getY() == i) {
                                if (e instanceof Wall) mapChar = '#';
                                else if (e instanceof Brick) mapChar = '*';
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

    public BombermanGame(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    private AnimationTimer gameLoop;
    private void pauseGame() {
        if (gameLoop != null) {
            gameLoop.stop(); // Dừng game
        }

        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        // Tạo nền mờ đen để làm nổi bật menu pause
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        // Đặt pauseMenu phủ toàn bộ canvas
        pauseMenu.setPrefSize(canvas.getWidth(), canvas.getHeight());

        Button continueBtn = new Button("Continue");
        Button saveExitBtn = new Button("Save & Exit");

        pauseMenu.getChildren().addAll(continueBtn, saveExitBtn);

        // Lấy VBox cha (chứa InfoBar + Canvas)
        VBox root = (VBox) canvas.getParent();

        // Thêm pauseMenu vào cuối cùng để nó hiển thị trên cùng
        root.getChildren().add(pauseMenu);

        continueBtn.setOnAction(e -> {
            // Xóa pauseMenu khi tiếp tục
            root.getChildren().remove(pauseMenu);

            // Gọi hàm tiếp tục game, nếu bạn có thêm logic tải game thì gọi continueGame()
            // Hoặc nếu chỉ dừng tạm thì chỉ cần start lại AnimationTimer
            if (gameLoop != null) {
                gameLoop.start();
            }
        });

        saveExitBtn.setOnAction(e -> {
            try {
                saveGame("res/savegame.txt");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Quay về màn hình Start Menu
            mainApp.showStartMenu();

            // Đồng thời xóa menu pause để tránh lỗi khi quay lại
            root.getChildren().remove(pauseMenu);
        });
    }




}
