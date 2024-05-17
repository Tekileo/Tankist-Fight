import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends JFrame {

  private JButton startButton;
  private JTextArea resultTextArea;
  private int availablePort = -1;
  private ServerSocket serverSocket;
  private static boolean ingame = true;
  private ArrayList<ClientHandler> clients = new ArrayList<>();
  private ArrayList<Player> players = new ArrayList<>();
  private boolean sent = false;
  private static int sentToPlayers = 0; // New variable to track connected clients

  public Server() {
    setTitle("Tankist Server");
    setSize(300, 100);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    resultTextArea = new JTextArea();
    resultTextArea.setEditable(false);
    resultTextArea.setFont(new Font("Arial", Font.BOLD, 12));
    resultTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
    resultTextArea.setLineWrap(true);
    resultTextArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(resultTextArea);
    add(scrollPane, BorderLayout.CENTER);

    startButton = new JButton("Start");
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (startButton.getText().equals("Start")) {
          startServer();
        } else {
          stopServer();
        }
      }
    });
    add(startButton, BorderLayout.SOUTH);
  }

  private void startServer() {
    try {
      InetAddress localhost = InetAddress.getLocalHost();
      String host = localhost.getHostAddress();
      int startPort = 7000;
      int endPort = 8000;

      for (int port = startPort; port <= endPort; port++) {
        try {
          Socket socket = new Socket(host, port);
          System.out.println("Port " + port + " is in use.");
          socket.close();
        } catch (Exception ex) {
          availablePort = port;
          resultTextArea.setText(host + ":" + availablePort);
          break;
        }
      }

      serverSocket = new ServerSocket(availablePort);
      System.out.println("Server started on port: " + availablePort);

      Thread serverThread = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            while (ingame) {
              Socket clientSocket = serverSocket.accept();
              int clientNumber = clients.size() + 1;
              ClientHandler clientHandler = new ClientHandler(clientSocket, clientNumber);
              clients.add(clientHandler);
              clientHandler.start();
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      });
      serverThread.start();

      startButton.setText("Stop");
    } catch (Exception ex) {
      ex.printStackTrace();
      resultTextArea.setText("Error: " + ex.getMessage());
    }
  }

  private void stopServer() {
    try {
      ingame = false; // Set flag to stop accepting new clients
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
        resultTextArea.setText("Server stopped.");
      }
      // Terminate all client threads
      for (ClientHandler client : clients) {
        client.close();
      }
      clients.clear(); // Clear the list of clients
      startButton.setText("Start");
    } catch (Exception ex) {
      ex.printStackTrace();
      resultTextArea.setText("Error: " + ex.getMessage());
    }
  }

  private class ClientHandler extends Thread {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintStream output;
    private int clientNumber;
    private ObjectOutputStream objout;
    private String name;

    public ClientHandler(Socket socket, int clientNumber) throws IOException {
        this.clientSocket = socket;
        this.clientNumber = clientNumber;
        sentToPlayers++; // Increment counter for connected clients
        try {
            output = new PrintStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            objout = new ObjectOutputStream(socket.getOutputStream());
            name = input.readLine();
            System.out.println(name);
            
            if (clientNumber == 1) {
                Player player = new Player(200, 500, "green", 5, name);
            players.add(player);
            System.out.println("player1: " + player +"," +clientNumber);
        } else if (clientNumber == 2) {
            Player player = new Player(200, 500, "orange", 5, name);
            players.add(player);
            System.out.println("player2: " + player+"," +clientNumber);
        }
        sendPlayersListIfNeeded(); // Send player list upon connection
    } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
  
      @Override
      public void run() {
        try {
            while (!sent) {
                sendPlayersListIfNeeded(); // Call to send list on every client input
                
            }
            String clientInput;
          while ((clientInput = input.readLine()) != "corta") {
            System.out.println("Client" + clientNumber + ": " + clientInput);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          close();
        }
      }
  
      public void close() {
        try {
          if (input != null) {
            input.close();
          }
          if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
  
       public void sendPlayersListIfNeeded() throws IOException {
        if (players.size() >= 2 && clients.size() >=2) {
          // Use a loop to send the list to all connected clients
          for (ClientHandler client : clients) {
            System.out.println("Sent to client num : "+ client.clientNumber);
            client.objout.writeObject(players);
            sent = true;
          }
          System.out.println("Number of players sent: " + players.size());
        }
      }
      
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            Server gui = new Server();
            gui.setVisible(true);
          }
        });
      }
    }

  