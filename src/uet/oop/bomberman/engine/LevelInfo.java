package uet.oop.bomberman.engine;

public class LevelInfo {
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

    public int timeLeft;
    public int score;

    public LevelInfo(int level, int rows, int cols, char[][] map, int timeLeft, int score) {
        this.level = level;
        this.rows = rows;
        this.cols = cols;
        this.map = map;
        this.timeLeft = timeLeft;
        this.score = score;
    }

    public LevelInfo() {
        // Constructor mặc định, có thể để trống hoặc khởi tạo giá trị mặc định
    }
}