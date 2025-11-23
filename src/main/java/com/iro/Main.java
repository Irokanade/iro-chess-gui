package com.iro;

import com.iro.gui.GamePanel;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        String computerSide = "black";

        if (args.length > 0) {
            if (args[0].equals("white")) {
                computerSide = args[0];
            } else if (args[0].equals("black")) {
                computerSide = args[0];
            }
        }

        System.out.println("Computer side: " + computerSide);

        JFrame window = new JFrame("Iro Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Add GamePanel to the window
        GamePanel gamePanel = new GamePanel(computerSide);
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Cleaning up engine...");
                gamePanel.cleanup();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook - cleaning up engine...");
            gamePanel.cleanup();
        }));

        gamePanel.launchGame();
    }
}

