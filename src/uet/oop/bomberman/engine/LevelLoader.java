package uet.oop.bomberman.engine;

import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    public static class LevelInfo {
        public int level;
        public int rows;
        public int cols;
        public char[][] map;
        public int timeLeft; // Thời gian còn lại khi save game
        public int score;    // Điểm số khi save game

        // Constructor cho Level mới (không có timeLeft, score)
        public LevelInfo(int level, int rows, int cols, char[][] map) {
            this.level = level;
            this.rows = rows;
            this.cols = cols;
            this.map = map;
            this.timeLeft = 0; // Giá trị mặc định khi game mới
            this.score = 0;    // Giá trị mặc định khi game mới
        }

        // Constructor cho Level đã lưu (có timeLeft, score)
        public LevelInfo(int level, int rows, int cols, char[][] map, int timeLeft, int score) {
            this.level = level;
            this.rows = rows;
            this.cols = cols;
            this.map = map;
            this.timeLeft = timeLeft;
            this.score = score;
        }
    }

    public LevelInfo loadLevel(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String[] header = reader.readLine().split(" ");
        int level = Integer.parseInt(header[0]);
        int rows = Integer.parseInt(header[1]);
        int cols = Integer.parseInt(header[2]);

        char[][] map = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("File không đủ dữ liệu cho map ở dòng " + i);
            }
            for (int j = 0; j < cols; j++) {
                if (j < line.length()) {
                    map[i][j] = line.charAt(j);
                } else {
                    map[i][j] = ' '; // Mặc định là khoảng trắng nếu dòng ngắn hơn cột
                }
            }
        }
        reader.close();
        return new LevelInfo(level, rows, cols, map);
    }

    // Đã chỉnh sửa: Logic loadEntities để phân biệt 'x' và 'x*'
    public List<Entity> loadEntities(LevelInfo levelInfo) {
        // Sử dụng một danh sách chính để thu thập tất cả các entity
        List<Entity> entities = new ArrayList<>();

        // Bước 1: Tạo tất cả các Entity không liên quan đến Brick/Portal
        // Và đây cũng là nơi DUY NHẤT chúng ta tạo Bomber
        for (int i = 0; i < levelInfo.rows; i++) {
            for (int j = 0; j < levelInfo.cols; j++) {
                char c = levelInfo.map[i][j];
                switch (c) {
                    case 'p':
                        entities.add(new Bomber(j, i, Sprite.player_right.getFxImage()));
                        break;
                    case '1':
                        entities.add(new Balloom(j, i, Sprite.balloom_right1.getFxImage()));
                        break;
                    case '2':
                        entities.add(new Oneal(j, i, Sprite.oneal_right1.getFxImage()));
                        break;
                    // Bỏ qua '*' và 'x' ở đây, sẽ xử lý chúng ở bước 2
                }
            }
        }

        // Bước 2: Duyệt lại map để xử lý Brick và Portal
        // (đã di chuyển logic này lên để tránh tạo trùng Bomber)
        for (int i = 0; i < levelInfo.rows; i++) {
            for (int j = 0; j < levelInfo.cols; j++) {
                char c = levelInfo.map[i][j];
                if (c == 'x') {
                    boolean hasBrickCover = false;
                    if (j + 1 < levelInfo.cols && levelInfo.map[i][j + 1] == '*') {
                        hasBrickCover = true;
                    }

                    if (hasBrickCover) {
                        entities.add(new Brick(j, i, Sprite.brick.getFxImage())); // Brick che
                        entities.add(new Portal(j, i, Sprite.portal.getFxImage(), true)); // Portal ẩn
                    } else {
                        entities.add(new Portal(j, i, Sprite.portal.getFxImage(), false)); // Portal hiện
                    }
                } else if (c == '*') {
                    // Để đảm bảo không tạo trùng Brick từ "x*"
                    // Chỉ tạo Brick cho '*' nếu ô bên trái của nó không phải là 'x'
                    if (j == 0 || levelInfo.map[i][j-1] != 'x') {
                        entities.add(new Brick(j, i, Sprite.brick.getFxImage()));
                    }
                }
            }
        }
        return entities;
    }

    public List<Entity> loadStillObjects(LevelInfo levelInfo) {
        List<Entity> stillObjects = new ArrayList<>();
        for (int i = 0; i < levelInfo.rows; i++) {
            for (int j = 0; j < levelInfo.cols; j++) {
                char c = levelInfo.map[i][j];
                if (c == '#') {
                    stillObjects.add(new Wall(j, i, Sprite.wall.getFxImage()));
                } else {
                    stillObjects.add(new Grass(j, i, Sprite.grass.getFxImage()));
                }
            }
        }
        return stillObjects;
    }

    public LevelInfo loadSavedLevel(String filepath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));

        String[] header = reader.readLine().split(" ");
        int level = Integer.parseInt(header[0]);
        int rows = Integer.parseInt(header[1]);
        int cols = Integer.parseInt(header[2]);

        int timeLeft = 0;
        int score = 0;
        if (header.length >= 5) {
            timeLeft = Integer.parseInt(header[3]);
            score = Integer.parseInt(header[4]);
        }

        char[][] map = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("File không đủ dữ liệu cho map ở dòng " + i);
            }
            for (int j = 0; j < cols; j++) {
                if (j < line.length()) {
                    map[i][j] = line.charAt(j);
                } else {
                    map[i][j] = ' ';
                }
            }
        }
        reader.close();

        return new LevelInfo(level, rows, cols, map, timeLeft, score);
    }
}