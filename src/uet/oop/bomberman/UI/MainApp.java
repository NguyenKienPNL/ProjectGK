package uet.oop.bomberman.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.UI.GameResult; // Đảm bảo đã import GameResult

import java.io.IOException;
import java.io.File;

// Import các lớp cần thiết cho Media
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class MainApp extends Application {

    private Stage primaryStage;
    private BombermanGame currentRunningGame; // Biến để giữ instance của game đang chạy

    // MediaPlayer chung cho nhạc nền (Menu, Game Play)
    private MediaPlayer backgroundMusicPlayer;
    // MediaPlayer riêng cho các hiệu ứng âm thanh ngắn (Win, Game Over)
    private MediaPlayer soundEffectPlayer;

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

    /**
     * Phương thức tải và phát nhạc nền.
     * Nếu có nhạc nền đang phát, nó sẽ dừng và phát bài mới.
     * Nhạc nền sẽ được lặp lại vô hạn.
     * @param musicFilePath Đường dẫn đến file nhạc (ví dụ: "res/sounds/menu_music.mp3")
     */
    public void playBackgroundMusic(String musicFilePath) {
        // Dừng và giải phóng MediaPlayer cũ nếu đang chạy
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.dispose(); // Quan trọng để giải phóng tài nguyên
        }

        try {
            // Chuyển đổi đường dẫn file thành URI mà Media có thể hiểu
            String uriString = new File(musicFilePath).toURI().toString();
            Media media = new Media(uriString);

            backgroundMusicPlayer = new MediaPlayer(media);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Lặp lại vô hạn
            backgroundMusicPlayer.setVolume(0.5); // Điều chỉnh âm lượng (tùy chọn)
            backgroundMusicPlayer.play();
            System.out.println("Playing background music: " + musicFilePath); // Debug
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phương thức phát các hiệu ứng âm thanh ngắn (ví dụ: nhạc Win/GameOver).
     * Sẽ tạo một MediaPlayer mới và chỉ phát một lần.
     * @param soundFilePath Đường dẫn đến file âm thanh
     */
    public void playSoundEffect(String soundFilePath) {
        // Dừng và giải phóng hiệu ứng cũ nếu có (tránh chồng chéo nếu phát nhanh)
        if (soundEffectPlayer != null) {
            soundEffectPlayer.stop();
            soundEffectPlayer.dispose();
        }

        try {
            String uriString = new File(soundFilePath).toURI().toString();
            Media media = new Media(uriString);
            soundEffectPlayer = new MediaPlayer(media);
            soundEffectPlayer.setCycleCount(1); // Chỉ phát một lần
            soundEffectPlayer.setVolume(0.8); // Điều chỉnh âm lượng (tùy chọn)
            soundEffectPlayer.play();
            // Giải phóng tài nguyên sau khi phát xong để tránh rò rỉ bộ nhớ
            soundEffectPlayer.setOnEndOfMedia(() -> {
                if (soundEffectPlayer != null) {
                    soundEffectPlayer.dispose();
                    soundEffectPlayer = null; // Đặt về null sau khi dispose
                }
            });
            System.out.println("Playing sound effect: " + soundFilePath); // Debug
        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Phương thức dừng nhạc nền đang phát.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.dispose();
            backgroundMusicPlayer = null; // Đặt về null sau khi dispose
            System.out.println("Background music stopped."); // Debug
        }
    }

    /**
     * Phương thức dừng hiệu ứng âm thanh đang phát.
     */
    public void stopSoundEffect() {
        if (soundEffectPlayer != null) {
            soundEffectPlayer.stop();
            soundEffectPlayer.dispose();
            soundEffectPlayer = null;
            System.out.println("Sound effect stopped.");
        }
    }


    // Phương thức khởi động game mới
    public void startGame() {
        // Dừng nhạc menu và phát nhạc game
        stopBackgroundMusic();
        stopSoundEffect(); // Đảm bảo dừng nhạc win/lose nếu có
        playBackgroundMusic("res/sounds/game_music.mp3"); // Đường dẫn đến nhạc game

        currentRunningGame = new BombermanGame(this, primaryStage);
        try {
            currentRunningGame.start(primaryStage);
            currentRunningGame.createMap();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error starting new game: " + e.getMessage());
        }
    }


    // Phương thức tiếp tục game
    public void continueGame() {
        File saveFile = new File("res/savegame.txt");
        if (!saveFile.exists()) {
            System.out.println("No saved game found. Cannot continue. Starting a new new game.");
            startGame(); // Nếu không có save, bắt đầu game mới
            return;
        }

        // Dừng nhạc menu và phát nhạc game
        stopBackgroundMusic();
        stopSoundEffect(); // Đảm bảo dừng nhạc win/lose nếu có
        playBackgroundMusic("res/sounds/game_music.mp3"); // Đường dẫn đến nhạc game

        currentRunningGame = new BombermanGame(this, primaryStage);
        try {
            currentRunningGame.start(primaryStage);
            currentRunningGame.continueGame();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error continuing game: " + e.getMessage());
            showStartMenu(); // Quay về start menu nếu có lỗi
        }
    }

    // Phương thức hiển thị StartMenu
    public void showStartMenu() {
        // Dừng bất kỳ nhạc nào đang phát và phát nhạc menu
        stopBackgroundMusic();
        stopSoundEffect(); // Đảm bảo dừng nhạc win/lose nếu có
        playBackgroundMusic("res/sounds/menu_music.mp3"); // Đường dẫn đến nhạc menu

        StartMenu startMenu = new StartMenu(this);
        Scene scene = new Scene(startMenu);
        primaryStage.setScene(scene);
    }

    /**
     * Phương thức hiển thị màn hình kết quả game (Win/Lose).
     * @param result Kết quả game (GameResult.WIN hoặc GameResult.LOSE)
     */
    public void showGameResult(GameResult result) {
        // Dừng nhạc nền game (nếu đang phát)
        stopBackgroundMusic();

        // Phát nhạc tương ứng với kết quả
        if (result == GameResult.WIN) {
            playSoundEffect("res/sounds/win_music.mp3"); // Đường dẫn đến nhạc win
        } else { // GameResult.LOSE
            playSoundEffect("res/sounds/gameover_music.mp3"); // Đường dẫn đến nhạc game over
        }

        GameResultScreen resultScreen = new GameResultScreen(this, result);
        Scene scene = new Scene(resultScreen);
        primaryStage.setScene(scene);
    }

    // Quan trọng: Dừng tất cả nhạc khi ứng dụng tắt hoàn toàn
    @Override
    public void stop() throws Exception {
        super.stop();
        stopBackgroundMusic();
        stopSoundEffect();
    }
}