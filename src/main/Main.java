package main;

import javax.swing.JFrame;

public class Main {

    public static JFrame window;
    public static void main(String[] args){
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Kyle Testgame");

        GamePanel gamePanel = new GamePanel();

        window.add(gamePanel);

        gamePanel.config.loadConfig();
        if(gamePanel.fullScreenOn == true){
          window.setUndecorated(true);
        }

        window.pack(); // causes this window to be sized to fit the preffered size and layout of its subcomponents


        window.setLocationRelativeTo(null); //purpose to set window on the center of the screen
        window.setVisible(true);

        gamePanel.setUpGame();
        gamePanel.startGameThread();
    }
}
