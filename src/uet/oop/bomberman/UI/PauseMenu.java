package uet.oop.bomberman.UI;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uet.oop.bomberman.BombermanGame;

import java.io.IOException;

public class PauseMenu {

    private final BombermanGame game;

    public PauseMenu(BombermanGame game) {
        this.game = game;
    }

    public void showPauseMenu(Stage stage) {
        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);

        Button continueButton = new Button("Continue");
        Button exitButton = new Button("Save & Exit");

        continueButton.setOnAction(e -> {
            game.continueGame(); // gọi resumeGame từ BombermanGame
        });

        exitButton.setOnAction(e -> {
            try {
                game.saveGame("res/savegame.txt");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            stage.close();         // thoát chương trình
        });

        menuLayout.getChildren().addAll(continueButton, exitButton);

        Scene pauseScene = new Scene(menuLayout, 400, 300);
        stage.setScene(pauseScene);
    }
}
