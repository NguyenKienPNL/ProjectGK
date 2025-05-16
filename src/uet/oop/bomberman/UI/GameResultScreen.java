package uet.oop.bomberman.UI;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import uet.oop.bomberman.UI.GameResult;


public class GameResultScreen extends VBox {

    public GameResultScreen(MainApp mainApp, GameResult result) {
        setStyle("-fx-background-color: black;");
        setPrefSize(960, 480);
        setAlignment(Pos.CENTER);
        setSpacing(20);

        String message = (result == GameResult.WIN) ? "YOU WIN!" : "GAME OVER";
        Color textColor = (result == GameResult.WIN) ? Color.LIMEGREEN : Color.RED;
        Color shadowColor = (result == GameResult.WIN) ? Color.DARKGREEN : Color.DARKRED;

        Button resultLabel = new Button(message);
        resultLabel.setStyle(
                "-fx-font-size: 110px;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-text-fill: " + toWebColor(textColor) + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: transparent;"
        );
        resultLabel.setDisable(true);

        DropShadow shadow = new DropShadow();
        shadow.setColor(shadowColor);
        shadow.setRadius(20);
        shadow.setOffsetX(0);
        shadow.setOffsetY(0);
        shadow.setSpread(0.8);
        resultLabel.setEffect(shadow);

        Button restartBtn = new Button("▶ RESTART");
        restartBtn.setStyle("-fx-font-size: 24px; -fx-font-family: 'Courier New'; -fx-text-fill: white; -fx-background-color: black; -fx-border-color: white; -fx-border-width: 2;");
        restartBtn.setOnAction(e -> mainApp.startGame());

        Button menuBtn = new Button("MAIN MENU");
        menuBtn.setStyle("-fx-font-size: 24px; -fx-font-family: 'Courier New'; -fx-text-fill: white; -fx-background-color: black; -fx-border-color: white; -fx-border-width: 2;");
        menuBtn.setOnAction(e -> mainApp.showStartMenu());

        getChildren().addAll(resultLabel, restartBtn, menuBtn);
    }

    private String toWebColor(Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }
}
