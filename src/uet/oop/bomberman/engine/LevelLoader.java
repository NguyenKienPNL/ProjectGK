package uet.oop.bomberman.engine;

import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.graphics.SpriteSheet;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class LevelLoader {
    public static class LevelInfo{
        public int level;
        public int rows;
        public int cols;
        public char [][] map;
        public LevelInfo(int level, int rows, int cols, char [][] map){
            this.level = level;
            this.rows = rows;
            this.cols = cols;
            this.map = map;
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

    public LevelInfo loadSavedLevel(String filePath) throws IOException {
        // Đọc file lưu trạng thái game
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // Đọc thông tin cấp độ, hàng và cột từ file lưu
        String[] header = reader.readLine().split(" ");
        int level = Integer.parseInt(header[0]);
        int rows = Integer.parseInt(header[1]);
        int cols = Integer.parseInt(header[2]);

        // Tạo mảng 2 chiều cho bản đồ
        char[][] map = new char[rows][cols];

        // Đọc từng dòng của bản đồ từ file
        for (int i = 0; i < rows; i++) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("File không đủ dữ liệu");
            }
            for (int j = 0; j < cols; j++) {
                if (j < line.length()) {
                    map[i][j] = line.charAt(j);
                } else {
                    map[i][j] = ' '; // Điền vào dấu cách nếu thiếu
                }
            }
        }
        reader.close();

        return new LevelInfo(level, rows, cols, map);
    }

}