package uet.oop.bomberman.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements Runnable {

    public static final int TILE_SIZE = 32;
    public static final int MAX_COL = 30;
    public static final int MAX_ROW = 15;
    public static final int WIDTH = TILE_SIZE * MAX_COL;   // 960
    public static final int HEIGHT = TILE_SIZE * MAX_ROW;  // 480

    private Thread gameThread;
    private boolean running = false;

    // Điều khiển
    private KeyHandler keyH = new KeyHandler();

    // Tọa độ nhân vật
    private int playerX = 100;
    private int playerY = 100;
    private int speed = 4;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(keyH);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        running = true;
        final double FPS = 60.0;
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (keyH.upPressed)    playerY -= speed;
        if (keyH.downPressed)  playerY += speed;
        if (keyH.leftPressed)  playerX -= speed;
        if (keyH.rightPressed) playerX += speed;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.GREEN);
        g2.fillRect(playerX, playerY, TILE_SIZE, TILE_SIZE);
    }

    // Lớp xử lý phím
    public static class KeyHandler implements KeyListener {

        public boolean upPressed, downPressed, leftPressed, rightPressed;

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_W) upPressed = false;
            if (code == KeyEvent.VK_S) downPressed = false;
            if (code == KeyEvent.VK_A) leftPressed = false;
            if (code == KeyEvent.VK_D) rightPressed = false;
        }
    }
}
