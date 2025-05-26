package uet.oop.bomberman.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color; // Import Color
import javafx.scene.text.Font;   // Import Font
import javafx.scene.text.FontWeight; // Import FontWeight

public class InfoBar extends HBox {

    private Label timeLabel;
    private Label scoreLabel;
    private Label levelLabel;
    private Button pauseButton;

    public InfoBar() {
        setStyle("-fx-background-color: #222;");
        setPadding(new Insets(10));
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);

        timeLabel = new Label("Time: 0");
        scoreLabel = new Label("Score: 0");
        levelLabel = new Label("Level: 1");
        pauseButton = new Button("||");
        pauseButton.setFocusTraversable(false);


        // --- BẮT ĐẦU THAY ĐỔI KIỂU CHỮ VÀ MÀU SẮC CHO CÁC LABEL ---
        Font labelFont = Font.font("Courier New", FontWeight.BOLD, 18); // Font đậm, kích thước 18
        Color labelColor = Color.WHITE; // Màu trắng

        timeLabel.setFont(labelFont);
        timeLabel.setTextFill(labelColor);

        scoreLabel.setFont(labelFont);
        scoreLabel.setTextFill(labelColor);

        levelLabel.setFont(labelFont);
        levelLabel.setTextFill(labelColor);
        // --- KẾT THÚC THAY ĐỔI ---

        Region spacer = new Region(); // để đẩy pause button về phải
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(timeLabel, scoreLabel, levelLabel, spacer, pauseButton);
    }

    public void setTime(int time) {
        timeLabel.setText("Time: " + time);
    }

    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void setLevel(int level) {
        levelLabel.setText("Level: " + level);
    }

    public Button getPauseButton() {
        return pauseButton;
    }
}
