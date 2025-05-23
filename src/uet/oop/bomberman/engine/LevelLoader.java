package uet.oop.bomberman.engine;

import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.graphics.SpriteSheet;
import uet.oop.bomberman.engine.LevelInfo;



import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class LevelLoader {
    public static class LevelInfo {
        public int level;
        public int rows;
        public int cols;
        public char[][] map;
        public int timeLeft;
        public int score;

        // Constructor chỉ có level, rows, cols, map
        public LevelInfo(int level, int rows, int cols, char[][] map) {
            this.level = level;
            this.rows = rows;
            this.cols = cols;
            this.map = map;
            this.timeLeft = 0;  // mặc định
            this.score = 0;     // mặc định
        }

        // Constructor có thêm timeLeft và score
        public LevelInfo(int level, int rows, int cols, char[][] map, int timeLeft, int score) {
            this.level = level;
            this.rows = rows;
            this.cols = cols;
            this.map = map;
            this.timeLeft = timeLeft;
            this.score = score;
        }
    }

    public LevelInfo loadLevel(String filepath) throws IOException{
//        Đọc có bộ nhớ đệm
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
//      Đọc dòng đầu tiên chứa thông tin màn chơi.
        String[] header = reader.readLine().split(" ");
        int level = Integer.parseInt(header[0]);
        int rows = Integer.parseInt(header[1]);
        int cols = Integer.parseInt(header[2]);
//        Tạo mảng 2 chiều cho bản đồ.
        char [][] map = new char[rows][cols];
//        đọc từng dòng của bản đồ .
        for(int i = 0; i < rows; i++) {
            String line = reader.readLine();
            if(line == null) {
                throw new IOException("file khong du du lieu");
            }
//            Đảm bảo mỗi dòng đúng số cột
            for(int j = 0; j < cols; j++) {
                if(j < line.length()) {
                    map[i][j] = line.charAt(j);
                }
                else {
                    map[i][j] = ' ';
                }
            }
        }
        reader.close();
        return new LevelInfo(level, rows, cols, map);
    }

    public ArrayList<Entity> loadEntities(LevelInfo levelInfo) {
        ArrayList<Entity> entities = new ArrayList<>();
        for (int i = 0; i < levelInfo.rows; i++) {
            for (int j = 0; j < levelInfo.cols; j++) {
                if (levelInfo.map[i][j] == 'p') {
                    entities.add(new Bomber(j, i, Sprite.player_right.getFxImage()));
                } else if (levelInfo.map[i][j] == '1') {
                    entities.add(new Balloom(j, i, Sprite.balloom_right1.getFxImage()));
                } else if (levelInfo.map[i][j] == '2') {
                    entities.add(new Oneal(j, i, Sprite.oneal_right1.getFxImage()));
                } else if (levelInfo.map[i][j] == '*') {
                    entities.add(new Brick(j, i, Sprite.brick.getFxImage()));
                }
            }
        }
        return entities;
    }

    public ArrayList<Entity> loadStillObjects(LevelInfo levelInfo) {
        ArrayList<Entity> StillObjects = new ArrayList<>();
        for (int i = 0; i < levelInfo.rows; i++) {
            for (int j = 0; j < levelInfo.cols; j++) {
                if (levelInfo.map[i][j] == '#') {
                    StillObjects.add(new Wall(j, i, Sprite.wall.getFxImage()));
                } else {
                    StillObjects.add(new Grass(j, i, Sprite.grass.getFxImage()));
                }
            }
        }
        return StillObjects;
    }

    public LevelInfo loadSavedLevel(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

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
                throw new IOException("File không đủ dữ liệu");
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