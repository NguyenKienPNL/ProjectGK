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
}