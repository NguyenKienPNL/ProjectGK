package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group; // Có thể không cần nếu không dùng Group
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
import javafx.scene.layout.StackPane; // Import StackPane
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Font; // Import Font
import javafx.scene.paint.Color; // Import Color



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BombermanGame extends Application {

    public static final int WIDTH = 30;
    public static final int HEIGHT = 15;

    public char[][] map;

    private GraphicsContext gc;
    private Canvas canvas;
    private Stage stage; // Giữ tham chiếu đến Stage

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


    // Constructor mới để nhận cả MainApp và Stage
    public BombermanGame(MainApp mainApp, Stage stage) {
        this.mainApp = mainApp;
        this.stage = stage;
        this.infoBar = new InfoBar(); // Khởi tạo InfoBar ở đây
    }

    public BombermanGame() {} // Giữ lại constructor mặc định nếu cần


    public static Bomber getBomberman() {
        return bomberman;
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Đảm bảo stage và infoBar đã được thiết lập từ constructor
        // Nếu BombermanGame được khởi tạo từ MainApp, stage và infoBar đã có giá trị.
        // Dòng này chỉ cần thiết nếu BombermanGame được gọi trực tiếp qua Application.launch()
        if (this.stage == null) {
            this.stage = stage;
        }
        if (this.infoBar == null) {
            this.infoBar = new InfoBar();
        }

        // Canvas game
        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();

        // Tạo một VBox để chứa InfoBar và Canvas game
        VBox gameContent = new VBox();
        gameContent.getChildren().addAll(infoBar, canvas);

        // Tạo StackPane làm root chính để có thể chồng các lớp UI lên nhau
        StackPane root = new StackPane();
        root.getChildren().add(gameContent); // Thêm nội dung game vào StackPane

        Scene scene = new Scene(root);
        this.stage.setScene(scene); // BombermanGame tự set Scene lên Stage của nó
        this.stage.show(); // BombermanGame tự show Stage của nó

        lastTimer = (int) System.currentTimeMillis();

        // Đặt sự kiện cho nút pause MỘT LẦN KHI GAME KHỞI ĐỘNG
        infoBar.getPauseButton().setOnAction(e -> pauseGame());

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();

                frames++;
                if (now - lastTimer >= 1_000_000_000) {
                    fps = frames;
//                    System.out.println("FPS: " + fps);
                    frames = 0;
                    lastTimer = now;
                    // Cập nhật tiêu đề Stage của BombermanGame
                    if (BombermanGame.this.stage != null) {
                        BombermanGame.this.stage.setTitle("Bomberman FPS: " + fps);
                    }


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
                }
            }
        };
        gameLoop.start();
        // Không return scene ở đây vì start() phải là void
    }


    public void createMap() throws IOException {
        // Xóa các danh sách static để đảm bảo bản đồ sạch cho game mới/cấp độ mới
        entities.clear();
        stillObjects.clear();

        LevelLoader levelLoader = new LevelLoader();
        LevelLoader.LevelInfo levelInfo = levelLoader.loadLevel("res/levels/level1.txt"); // Giả sử level1.txt cho game mới

        // Lấy map
        map = levelInfo.map;

        // Load object tĩnh
        stillObjects = levelLoader.loadStillObjects(levelInfo);

        // Load các entity động
        List<Entity> loadedEntities = levelLoader.loadEntities(levelInfo);
        entities.addAll(loadedEntities);

        // Tìm Bomber sau khi map được tạo
        for (Entity e : entities) {
            if (e instanceof Bomber) {
                bomberman = (Bomber) e;
                break;
            }
        }
        // Gắn sự kiện bàn phím cho Bomber sau khi nó được tìm thấy
        // Đảm bảo stage và scene không null trước khi gắn sự kiện
        if (bomberman != null && stage != null && stage.getScene() != null) {
            bomberman.handleKeyEvent(stage.getScene());
        } else {
            System.err.println("Warning: Could not attach key event handler to Bomber. Bomber or Scene is null.");
        }
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
            if (e instanceof Brick && x == e.getX() && e.getY() == y && !((Brick) e).isDestroyed()) {
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
            LevelLoader levelLoader = new LevelLoader();
            LevelLoader.LevelInfo levelInfo = levelLoader.loadSavedLevel("res/savegame.txt");

            // Xóa các danh sách static trước khi tải trạng thái đã lưu
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

            // Gắn sự kiện bàn phím cho Bomber sau khi nó được tìm thấy
            if (bomberman != null && stage != null && stage.getScene() != null) {
                bomberman.handleKeyEvent(stage.getScene());
            } else {
                System.err.println("Bomberman not found after loading saved game or Scene is null. Returning to Start Menu.");
                if (mainApp != null) {
                    mainApp.showStartMenu(); // Quay về start menu
                }
                return;
            }


            if (gameLoop != null) {
                gameLoop.start();
            } else {
                System.err.println("gameLoop is null in continueGame. This should not happen if start() was called.");
                // Nếu gameLoop là null ở đây, có thể có vấn đề với luồng khởi tạo.
                // Để đảm bảo, có thể khởi tạo lại gameLoop ở đây nếu cần, nhưng tốt nhất là tránh.
            }

            this.timeLeft = levelInfo.timeLeft;
            this.score = levelInfo.score;
            this.currentLevel = levelInfo.level;
            // Cập nhật InfoBar với các giá trị đã tải
            infoBar.setTime(this.timeLeft);
            infoBar.setScore(this.score);
            infoBar.setLevel(this.currentLevel);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading saved game. Returning to Start Menu.");
            if (mainApp != null) {
                mainApp.showStartMenu(); // Quay về start menu nếu lỗi
            }
        }


    }

    public void saveGame(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // 1. Lưu Level, Width, Height
            writer.write(bomberman.getCurrentLevel() + " " + WIDTH + " " + HEIGHT);
            writer.newLine();

            // 2. Lưu thời gian và điểm
            writer.write(timeLeft + " " + score); // Thay đổi timeRemaining thành timeLeft
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

    private AnimationTimer gameLoop;
    private void pauseGame() {
        System.out.println("Pause button clicked. Attempting to pause game."); // Debug print

        if (gameLoop != null) {
            gameLoop.stop(); // Dừng game
            System.out.println("Game loop stopped."); // Debug print
        }

        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);
        // Tạo nền mờ đen để làm nổi bật menu pause
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        // Đặt pauseMenu phủ toàn bộ canvas
        // Sử dụng binding để menu luôn có kích thước bằng root
        pauseMenu.prefWidthProperty().bind(stage.getScene().widthProperty());
        pauseMenu.prefHeightProperty().bind(stage.getScene().heightProperty());
        System.out.println("Pause menu created with size: " + canvas.getWidth() + "x" + canvas.getHeight()); // Debug print


        Button continueBtn = new Button("Continue");
        Button saveExitBtn = new Button("Save & Exit");

        // Cải thiện styling cho các nút để dễ nhìn hơn
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

        // Thêm hiệu ứng khi hover
        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(buttonStyle + "-fx-background-color: #666;"));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle(buttonStyle));
        saveExitBtn.setOnMouseEntered(e -> saveExitBtn.setStyle(buttonStyle + "-fx-background-color: #666;"));
        saveExitBtn.setOnMouseExited(e -> saveExitBtn.setStyle(buttonStyle));


        pauseMenu.getChildren().addAll(continueBtn, saveExitBtn);

        // Lấy StackPane cha (root)
        StackPane root = (StackPane) stage.getScene().getRoot(); // Lấy root từ Scene
        if (root == null) {
            System.err.println("Error: Root StackPane is null. Cannot add pause menu."); // Debug print
            return; // Thoát nếu root null
        }
        System.out.println("Root StackPane found. Adding pause menu to root."); // Debug print


        // Thêm pauseMenu vào StackPane để nó hiển thị trên cùng
        root.getChildren().add(pauseMenu);
        System.out.println("Pause menu added to root. Children count: " + root.getChildren().size()); // Debug print


        continueBtn.setOnAction(e -> {
            System.out.println("Continue button clicked."); // Debug print
            // Xóa pauseMenu khi tiếp tục
            root.getChildren().remove(pauseMenu);

            // Gọi hàm tiếp tục game, nếu bạn có thêm logic tải game thì gọi continueGame()
            // Hoặc nếu chỉ dừng tạm thì chỉ cần start lại AnimationTimer
            if (gameLoop != null) {
                gameLoop.start();
                System.out.println("Game loop started."); // Debug print
            }
        });

        saveExitBtn.setOnAction(e -> {
            System.out.println("Save & Exit button clicked."); // Debug print
            try {
                saveGame("res/savegame.txt");
                System.out.println("Game saved."); // Debug print
                mainApp.showStartMenu();
            } catch (IOException ex) {
                ex.printStackTrace();
                System.err.println("Error saving game: " + ex.getMessage()); // Debug print
            }

            // Quay về màn hình Start Menu
            mainApp.showStartMenu();

            // Đồng thời xóa menu pause để tránh lỗi khi quay lại
            root.getChildren().remove(pauseMenu);
            System.out.println("Pause menu removed and returning to Start Menu."); // Debug print
        });
    }
}
