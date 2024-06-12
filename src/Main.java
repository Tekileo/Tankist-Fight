import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.File;
import java.io.IOException;

public class Main {
    
    private static Clip backgroundMusicClip;
    private static boolean online = false;
    private static boolean isHost = false;
    private static String host = "localhost";
    private static final int port = 7000;
    private static ArrayList<Player> players = new ArrayList<>();
    private static JFrame gameFrame = new JFrame();
    private static JFrame mainMenuFrame = new JFrame();
    private static String username;

    public static void main(String[] args) {
        showMainMenu();
    }

    private static void showMainMenu() {
        mainMenuFrame.setSize(830, 600);
        mainMenuFrame.setTitle("Main Menu");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel();
        mainMenuFrame.add(panel);
        placeMainMenuComponents(panel);
        playBackgroundMusic("resources/BgMain.wav", -10.0f);

        mainMenuFrame.setVisible(true);
    }

    private static final String DB_URL = "jdbc:mysql://limines.duckdns.org:3306/tankist";
    private static final String DB_USER = "tankist";
    private static final String DB_PASSWORD = "TankistFight24";

    // Establish database connection
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void playBackgroundMusic(String filename, float volume) {
    try {
        File audioFile = new File(filename);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

        // Get a Clip object to play the audio
        backgroundMusicClip = AudioSystem.getClip();

        // Open the audio stream
        backgroundMusicClip.open(audioStream);

        // Get the FloatControl object associated with MASTER_GAIN
        FloatControl gainControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);

        // Set the volume level (in decibels)
        gainControl.setValue(volume);

        // Start playing the background music in a loop
        backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
        ex.printStackTrace();
    }
}

private static void stopBackgroundMusic() {
    if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
        backgroundMusicClip.stop();
    }
}

    // Check if user exists in the database
    private static boolean checkIfUserExists(String username) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Player WHERE name = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert new user into the database
    private static void insertNewUser(String username) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Player(name, connections, score) VALUES (?, 1, 0)")) {
            statement.setString(1, username);
            statement.executeUpdate();
            System.out.println("Añadido: "+ username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update connections count for an existing user
    private static void updateConnections(String username) {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("UPDATE Player SET connections = connections + 1 WHERE name = ?")) {
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void placeMainMenuComponents(JPanel panel) {
        panel.setLayout(null);

        JButton singlePlayerButton = new JButton("Single Player");
        styleButton(singlePlayerButton);
        singlePlayerButton.setBounds(340,400, 150, 30);
        singlePlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                online = false;
                try {
                    startGame(online);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(singlePlayerButton);

        JButton multiplayerButton = new JButton("Multiplayer");
        styleButton(multiplayerButton);
        multiplayerButton.setBounds(340, 450, 150, 30);
        multiplayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                online = true;
                showMultiplayerOptions();
            }
        });
        panel.add(multiplayerButton);

        JButton leaderboardButton = new JButton("Leaderboard");
        styleButton(leaderboardButton);
        leaderboardButton.setBounds(340, 500, 150, 30);
        leaderboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainMenuFrame.dispose();
                Leaderboard.showLeaderboard(mainMenuFrame);
            }
        });
        panel.add(leaderboardButton);
    }

    private static void showMultiplayerOptions() {
        mainMenuFrame.setVisible(false);
        JFrame multiplayerFrame = new JFrame("Multiplayer Options");
        multiplayerFrame.setSize(830, 600);
        multiplayerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        multiplayerFrame.setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel();
        multiplayerFrame.add(panel);
        placeMultiplayerComponents(panel, multiplayerFrame);

        multiplayerFrame.setVisible(true);
    }

    private static void placeMultiplayerComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        JButton clientButton = new JButton("Client");
        styleButton(clientButton);
        clientButton.setBounds(340, 350, 150, 35);
        clientButton.addActionListener(createClientActionListener(frame));
        panel.add(clientButton);

        JButton hostButton = new JButton("Host");
        styleButton(hostButton);
        hostButton.setBounds(340, 405, 150, 35);
        hostButton.addActionListener(createHostActionListener(frame));
        panel.add(hostButton);
    }

    private static ActionListener createClientActionListener(JFrame multiplayerFrame) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isHost = false;
                // Prompt user for username
                username = JOptionPane.showInputDialog(multiplayerFrame, "Enter Username:");
                if (username != null && !username.isEmpty()) { // Check if username is not empty
                    // Prompt user for IP and port
                    host = JOptionPane.showInputDialog(multiplayerFrame, "Enter IP:");
                    String portStr = JOptionPane.showInputDialog(multiplayerFrame, "Enter Port:");
                    try {
                        int port = Integer.parseInt(portStr);
                        Socket socket = new Socket(host, port);
    
                        // Check if username exists in the database
                        if (checkIfUserExists(username)) {
                            // If username exists, update connections count
                            updateConnections(username);
                        } else {
                            // If username doesn't exist, insert new row
                            insertNewUser(username);
                        }
    
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        multiplayerFrame.dispose();
                        showPlayButton(socket, out, in, false);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(multiplayerFrame, "Invalid port number. Please enter a valid integer port number.");
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(multiplayerFrame, "Error connecting to the server. Please check the IP and port.");
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(multiplayerFrame, "Please enter a valid username.");
                }
            }
        };
    }

   private static ActionListener createHostActionListener(JFrame multiplayerFrame) {
    return new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            isHost = true;
            // Prompt user for username
            username = JOptionPane.showInputDialog(multiplayerFrame, "Enter Username:");
            if (username != null && !username.isEmpty()) { // Check if username is not empty
                try {
                    InetAddress localHost = InetAddress.getLocalHost();
                    String localIp = localHost.getHostAddress();
                    ServerSocket serverSocket = new ServerSocket(port);
                    JOptionPane.showMessageDialog(multiplayerFrame, "Waiting for client to connect...\nIP: " + localIp + "\nPort: " + port);
                    Socket socket = serverSocket.accept();
                    // JOptionPane.showMessageDialog(multiplayerFrame, "Client connected.");

                    // Check if username exists in the database
                    if (checkIfUserExists(username)) {
                        // If username exists, update connections count
                        updateConnections(username);
                    } else {
                        // If username doesn't exist, insert new row
                        insertNewUser(username);
                    }

                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    multiplayerFrame.dispose();
                    showPlayButton(socket, out, in, true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(multiplayerFrame, "Error starting the server. Please try again.");
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(multiplayerFrame, "Please enter a valid username.");
            }
        }
    };
}

    private static void showPlayButton(Socket socket, ObjectOutputStream out, ObjectInputStream in, boolean isHost) {
        JButton playButton = new JButton("Play");
        styleButton(playButton);
        playButton.setBounds(365, 275, 100, 25);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    startGame(socket, out, in, isHost);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JFrame playFrame = new JFrame();
        playFrame.setSize(830, 600);
        playFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playFrame.setLocationRelativeTo(null);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        panel.add(playButton);

        playFrame.add(panel);
        playFrame.setVisible(true);
    }

    private static void startGame(boolean online) throws Exception {
        gameFrame.setSize(830, 635);
        gameFrame.setTitle("Tankist Fight");
        gameFrame.setBackground(Color.gray);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stopBackgroundMusic();
        Gameplay gamePlay = new Gameplay(online, null, false, null);
        gameFrame.add(gamePlay);
        gameFrame.setVisible(true);

        mainMenuFrame.setVisible(false);
    }

    private static void startGame(Socket socket, ObjectOutputStream out, ObjectInputStream in, boolean isHost) throws Exception {
        gameFrame.setSize(830, 635);
        gameFrame.setTitle("Tankist Fight");
        gameFrame.setBackground(Color.gray);
        gameFrame.setResizable(false);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        stopBackgroundMusic();
        Gameplay gamePlay = new Gameplay(online, socket, isHost, username);
        gameFrame.add(gamePlay);
        gameFrame.setVisible(true);

        mainMenuFrame.setVisible(false);
    }

    private static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.GRAY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
    }

    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("resources/TankistFightBf.jpeg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }
    }
}
