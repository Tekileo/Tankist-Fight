import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) throws Exception {
        JFrame gameFrame = new JFrame();
        Gameplay gamePlay;
        boolean online = true;
        final int port = 7000;
        final String host = "localhost";
        ArrayList<Player> players = new ArrayList<Player>();
        if (online) {
            try {
                System.out.println("Connecting to the server socket...");
                Socket socket = new Socket(host, port);
                PrintStream out = new PrintStream(socket.getOutputStream());
                //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Tadeo");
                System.out.println("Waiting for another player...");
                
                ObjectInputStream objin = new ObjectInputStream(socket.getInputStream());
                Object receivedObject;
                
                while ((receivedObject = objin.readObject()) != null) {
                    System.out.println("Received object size: " + ((ArrayList) receivedObject).size());
                    if (receivedObject instanceof ArrayList) { // Check if it's an ArrayList of players
                      players = (ArrayList<Player>) receivedObject;
                      System.out.println(players.size());
                      break; // Break out of the loop after receiving players
                    }
                  }
                  
                players = (ArrayList<Player>) receivedObject;
                System.out.println(players.size());
                gamePlay = new Gameplay(online, players, socket);
                gameFrame.setSize(800, 630);
                gameFrame.setTitle("Tankist Fight");
                gameFrame.setBackground(Color.gray);
                gameFrame.setResizable(false);
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.add(gamePlay);
                gameFrame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
                // Handle exception properly, e.g., display an error message
            }
        }else{
            gamePlay = new Gameplay(online, players, null);
                gameFrame.setSize(800, 630);
                gameFrame.setTitle("Tankist Fight");
                gameFrame.setBackground(Color.gray);
                gameFrame.setResizable(false);
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.add(gamePlay);
                gameFrame.setVisible(true);
            }

       
        // Pass the player information to the Gameplay constructor

        
       
    }
}
