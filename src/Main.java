import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main {

    private static boolean online = false;
    private static boolean isHost = false;
    private static String host = "localhost";
    private static final int port = 7000;
    private static ArrayList<Player> players = new ArrayList<>();
    private static JFrame gameFrame = new JFrame();
    private static JFrame mainMenuFrame = new JFrame();

    public static void main(String[] args) {
        showMainMenu();
    }

    private static void showMainMenu() {
        mainMenuFrame.setSize(400, 300);
        mainMenuFrame.setTitle("Main Menu");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        mainMenuFrame.add(panel);
        placeMainMenuComponents(panel);

        mainMenuFrame.setVisible(true);
    }

    private static void placeMainMenuComponents(JPanel panel) {
        panel.setLayout(null);

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.setBounds(120, 50, 150, 25);
        singlePlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                online = false;
                try {
                    startGame(online);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        panel.add(singlePlayerButton);

        JButton multiplayerButton = new JButton("Multiplayer");
        multiplayerButton.setBounds(120, 100, 150, 25);
        multiplayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                online = true;
                showMultiplayerOptions();
            }
        });
        panel.add(multiplayerButton);

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setBounds(120, 150, 150, 25);
        panel.add(leaderboardButton);
    }

    private static void showMultiplayerOptions() {
        mainMenuFrame.setVisible(false);
        JFrame multiplayerFrame = new JFrame("Multiplayer Options");
        multiplayerFrame.setSize(400, 200);
        multiplayerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        multiplayerFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        multiplayerFrame.add(panel);
        placeMultiplayerComponents(panel, multiplayerFrame);

        multiplayerFrame.setVisible(true);
    }

    private static void placeMultiplayerComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JButton clientButton = new JButton("Client");
        clientButton.setBounds(120, 50, 150, 25);
        clientButton.addActionListener(createClientActionListener(frame));
        panel.add(clientButton);

        JButton hostButton = new JButton("Host");
        hostButton.setBounds(120, 100, 150, 25);
        hostButton.addActionListener(createHostActionListener(frame));
        panel.add(hostButton);
    }

    private static ActionListener createClientActionListener(JFrame multiplayerFrame) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isHost = false;
                String ip = JOptionPane.showInputDialog("Enter IP:");
                String portStr = JOptionPane.showInputDialog("Enter Port:");
                host = ip;
                // Try to connect to the server here
                try {
                    int port = Integer.parseInt(portStr);
                    Socket socket = new Socket(host, port);
                    // Continue with the connection
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // Close the multiplayer options window and show play button
                    multiplayerFrame.dispose();
                    showPlayButton(socket, out, in, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private static ActionListener createHostActionListener(JFrame multiplayerFrame) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isHost = true;
                // Start server and wait for client connection
                try {
                    InetAddress localHost = InetAddress.getLocalHost();
                    String localIp = localHost.getHostAddress();
                    ServerSocket serverSocket = new ServerSocket(port);
                    JOptionPane.showMessageDialog(multiplayerFrame, "Waiting for client to connect...\nIP: " + localIp + "\nPort: " + port);
                    Socket socket = serverSocket.accept(); // Wait for connection
                    JOptionPane.showMessageDialog(multiplayerFrame, "Client connected.");
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
    
                    // Dispose of the multiplayer options window
                    multiplayerFrame.dispose();
                    // Show play button or start game here
                    showPlayButton(socket, out, in, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private static void showPlayButton(Socket socket, ObjectOutputStream out, ObjectInputStream in, boolean isHost) {
        JButton playButton = new JButton("Play");
        playButton.setBounds(150, 100, 100, 25);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    startGame(socket, out, in, isHost);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        JFrame playFrame = new JFrame();
        playFrame.setSize(400, 200);
        playFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(playButton);

        playFrame.add(panel);
        playFrame.setVisible(true);
    }

    private static void startGame(boolean online) throws Exception {
        gameFrame.setSize(800, 630);
        gameFrame.setTitle("Tankist Fight");
        gameFrame.setBackground(Color.gray);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Gameplay gamePlay = new Gameplay(false, null,  false);
        gameFrame.add(gamePlay);
        gameFrame.setVisible(true);

        mainMenuFrame.setVisible(false);
    }

    private static void startGame(Socket socket, ObjectOutputStream out, ObjectInputStream in, boolean isHost) throws Exception {
        gameFrame.setSize(800, 630);
        gameFrame.setTitle("Tankist Fight");
        gameFrame.setBackground(Color.gray);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Gameplay gamePlay = new Gameplay(online, socket, isHost);
        gameFrame.add(gamePlay);
        gameFrame.setVisible(true);

        mainMenuFrame.setVisible(false);
    }
}
