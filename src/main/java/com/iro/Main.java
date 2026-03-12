package com.iro;

import com.iro.gui.GamePanel;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        String opponent = "black";
        int depth = 6;

        for (String arg : args) {
            if (arg.startsWith("--opponent=")) {
                opponent = arg.substring("--opponent=".length());
            } else if (arg.startsWith("--depth=")) {
                depth = Integer.parseInt(arg.substring("--depth=".length()));
            }
        }

        if (!opponent.equals("white") && !opponent.equals("black") && !opponent.equals("human")) {
            System.out.println("Usage: --opponent=white|black|human --depth=<number>");
            System.exit(1);
        }

        System.out.println("Opponent: " + opponent + ", Depth: " + depth);

        JFrame window = new JFrame("Iro Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Add GamePanel to the window
        GamePanel gamePanel = new GamePanel(opponent, depth);
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cleaning up...");
            gamePanel.cleanup();
        }));

        gamePanel.launchGame();
    }
}

