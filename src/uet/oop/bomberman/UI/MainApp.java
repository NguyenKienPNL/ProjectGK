package uet.oop.bomberman.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.UI.GameResult;

import java.io.IOException;
import java.io.File; // Thêm import này


public class MainApp extends Application {

    private Stage primaryStage;
    private BombermanGame currentRunningGame; // Thêm biến để giữ instance của game đang chạy

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showStartMenu();  // Hiển thị Start Menu khi bắt đầu

        primaryStage.setTitle("Bomberman");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Phương thức khởi động game mới
    public void startGame() {
        // Luôn tạo một instance game mới khi bắt đầu game mới
        currentRunningGame = new BombermanGame(this, primaryStage); // Sử dụng constructor mới
        try {
            // Gọi start để thiết lập UI và Scene. BombermanGame.start() sẽ tự set Scene lên Stage.
            currentRunningGame.start(primaryStage);
            // Sau khi UI được thiết lập, gọi createMap để tải bản đồ mới
            currentRunningGame.createMap(); // Gọi createMap() ở đây cho game mới
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error starting new game: " + e.getMessage());
        }
    }


    // Phương thức tiếp tục game
    public void continueGame() {
        File saveFile = new File("res/savegame.txt");
        if (!saveFile.exists()) {
            System.out.println("No saved game found. Cannot continue. Starting a new game.");
            startGame(); // Nếu không có save, bắt đầu game mới
            return;
        }

        // Luôn tạo một instance BombermanGame mới khi tiếp tục game
        currentRunningGame = new BombermanGame(this, primaryStage); // Sử dụng constructor mới
        try {
            // Gọi start() để thiết lập UI và Scene cho game. BombermanGame.start() sẽ tự set Scene lên Stage.
            currentRunningGame.start(primaryStage);

            // Sau khi UI được thiết lập, gọi continueGame() để tải trạng thái đã lưu
            currentRunningGame.continueGame(); // Gọi continueGame() ở đây để tải save

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error continuing game: " + e.getMessage());
            showStartMenu(); // Quay về start menu nếu có lỗi
        }
    }

    // Phương thức hiển thị StartMenu
    public void showStartMenu() {
        StartMenu startMenu = new StartMenu(this);  // Truyền MainApp vào StartMenu
        Scene scene = new Scene(startMenu);
        primaryStage.setScene(scene);
    }

    public void showGameResult(GameResult result) {
        GameResultScreen resultScreen = new GameResultScreen(this, result);
        Scene scene = new Scene(resultScreen);
        primaryStage.setScene(scene);
    }

}
