import java.awt.Color;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame gameFrame = new JFrame();
        Gameplay gamePlay = new Gameplay();

        gameFrame.setSize(800, 630);
        gameFrame.setTitle("Tankist Fight");
        gameFrame.setBackground(Color.gray);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(gamePlay);
        gameFrame.setVisible(true);
    }

}
