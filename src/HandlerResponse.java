import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HandlerResponse implements Runnable {
    private static final int MOVE_SPEED = 2;
    private Player player;
    private Socket socket;
    private BufferedReader reader;
    private String from;

    public HandlerResponse(Player player, Socket socket, String from) {
        this.player = player;
        this.socket = socket;
        this.from = from;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Read the message from the other player
                String receivedMessage = reader.readLine();
                if (receivedMessage != null) {
                    System.out.println(from + ": " +receivedMessage);

                    processReceivedMessage(receivedMessage);
                // processReceivedMessage(receivedMessage);
                }
                System.out.println(from + ": " +receivedMessage);

                // Process the received message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(10); // Adjust sleep time as needed
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    // Method to process the received message
    private void processReceivedMessage(String message) {
        if (message != null && !message.isEmpty()) {
            switch (message) {
                
                case "W":
                System.out.println("arriba");
                player.setFacingPosition("up");
                    player.modifyPlayerY(-MOVE_SPEED); // Move up
                    break;
                case "S":
                System.out.println("abajo");
                player.setFacingPosition("down");
                    player.modifyPlayerY(MOVE_SPEED); // Move down
                    break;
                case "A":
                System.out.println("izquierda");
                player.setFacingPosition("left");
                    player.modifyPlayerX(-MOVE_SPEED); // Move left
                    break;
                case "D":
                System.out.println("derecha");
                player.setFacingPosition("right");
                    player.modifyPlayerX(MOVE_SPEED); // Move right
                    break;
                case "U":
                System.out.println("Dispara");
                player.shoot();
                    break;
                // Add more cases for other key codes if needed
                default:
                    // Handle other messages or ignore them
                    break;
            }
        }
    }
}
