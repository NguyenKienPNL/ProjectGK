package uet.oop.bomberman.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.UI.GameResult;

import java.io.IOException;


public class MainApp extends Application {

    private Stage primaryStage;

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

    // Phương thức khởi động game
    public void startGame() {
        BombermanGame game = new BombermanGame();  // ✅ Truyền this
        try {
            game.start(primaryStage); // Khởi chạy với stage đã có
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Phương thức tiếp tục game (mới)
    public void continueGame() {
        BombermanGame bombermanGame = new BombermanGame(primaryStage);
        try {
            bombermanGame.continueGame();  // Tiếp tục game từ nơi đã dừng lại
        } catch (Exception e) {
            e.printStackTrace();  // Hiển thị lỗi nếu có
        }
    }

    // Phương thức hiển thị StartMenu
    public void showStartMenu() {
        StartMenu startMenu = new StartMenu(this);  // Truyền MainApp vào StartMenu
        Scene scene = new Scene(startMenu);
        primaryStage.setScene(scene);
    }

//    // Phương thức hiển thị GameOverScreen
//    public void showGameOver() {
//        GameResultScreen gameOverScreen = new GameResultScreen(this);  // Truyền MainApp vào GameOverScreen
//        Scene scene = new Scene(gameOverScreen);
//        primaryStage.setScene(scene);
//    }

    public void showGameResult(GameResult result) {
        GameResultScreen resultScreen = new GameResultScreen(this, result);
        Scene scene = new Scene(resultScreen);
        primaryStage.setScene(scene);
    }

}
