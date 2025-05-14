package uet.oop.bomberman.UI;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import uet.oop.bomberman.UI.MainApp;

public class GameOverScreen extends VBox {

    public GameOverScreen(MainApp mainApp) {
        setStyle("-fx-background-color: darkred;");
        setPrefSize(960, 480);
        setAlignment(Pos.CENTER);
        setSpacing(20);

        Button gameOverLabel = new Button("GAME OVER");
        gameOverLabel.setStyle("-fx-font-size: 50px; -fx-text-fill: white; -fx-font-family: Monospaced;");
        gameOverLabel.setDisable(true);

        Button restartBtn = new Button("RESTART");
        restartBtn.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-background-color: black;");
        restartBtn.setOnAction(e -> mainApp.startGame());  // Gọi startGame() khi nhấn RESTART

        Button backToMenuBtn = new Button("MAIN MENU");
        backToMenuBtn.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-background-color: black;");
        backToMenuBtn.setOnAction(e -> mainApp.showStartMenu());  // Quay lại màn hình chính

        getChildren().addAll(gameOverLabel, restartBtn, backToMenuBtn);
    }
}
