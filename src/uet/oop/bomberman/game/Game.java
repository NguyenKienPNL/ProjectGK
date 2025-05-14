package uet.oop.bomberman.game;

import javax.swing.*;

public class Game {
    public Game() {
        JFrame frame = new JFrame("Bomberman");
        GamePanel gamePanel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.startGameThread();
    }
}

