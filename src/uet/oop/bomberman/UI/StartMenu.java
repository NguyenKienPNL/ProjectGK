package uet.oop.bomberman.UI;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import uet.oop.bomberman.UI.MainApp;

public class StartMenu extends VBox {

    public StartMenu(MainApp mainApp) {
        setStyle("-fx-background-color: black;");
        setPrefSize(960, 480);
        setAlignment(Pos.CENTER);
        setSpacing(20);

        Button title = new Button("BOMBERMAN");
        title.setStyle(
                "-fx-font-size: 120px;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-text-fill: #FFFF00;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: transparent;"
        );

        DropShadow redOutline = new DropShadow();
        redOutline.setColor(Color.RED);
        redOutline.setRadius(20);
        redOutline.setOffsetX(0);
        redOutline.setOffsetY(0);
        redOutline.setSpread(0.8);
        title.setEffect(redOutline);

        Button startBtn = new Button("▶ START");
        startBtn.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;"
        );
        startBtn.setOnAction(e -> mainApp.startGame());

        // ▶ CONTINUE button
        Button continueBtn = new Button("▶ CONTINUE");
        continueBtn.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;"
        );
        continueBtn.setOnAction(e -> mainApp.continueGame());  // Phải implement hàm này trong MainApp

        Button exitBtn = new Button("EXIT");
        exitBtn.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-family: 'Courier New';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: black;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2;"
        );
        exitBtn.setOnAction(e -> System.exit(0));

        getChildren().addAll(title, startBtn, continueBtn, exitBtn);
    }
}
