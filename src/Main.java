import java.awt.Color;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame gameFrame = new JFrame();
        boolean online = false;
        final int port = 7000;
        final String host = "localhost";
        
        try {
            System.out.println("Conectando al Socket del server...");
            Socket socket = new Socket(host, port);
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println("Testing 1");
            socket.close();
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        Gameplay gamePlay = new Gameplay(online);

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
