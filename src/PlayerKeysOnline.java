import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerKeysOnline implements KeyListener, Runnable {
    private Player player;
    private Socket con;
    private BufferedReader in;
    private PrintWriter out;
    private boolean imPlayer1;
    private int keyUp;
    private int keyDown;
    private int keyLeft;
    private int keyRight;
    private Thread playerKeysThread1;
	private Thread playerKeysThread2;
    private PlayerKeys pk1;
	private PlayerKeys pk2;
    private int keyShoot;
    private boolean spacePressed = false;
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private static final int MOVE_SPEED = 2;

    public PlayerKeysOnline(Player player, Socket con, boolean imPlayer1) throws Exception {
        this.con = con;
        this.imPlayer1 = imPlayer1;
        this.player = player;
        keyUp = KeyEvent.VK_W;
        keyDown = KeyEvent.VK_S;
        keyLeft = KeyEvent.VK_A;
        keyRight = KeyEvent.VK_D;
        keyShoot = KeyEvent.VK_U;
        if(imPlayer1){
            pk1 = new PlayerKeys(player, "left", false);
            pk2 = new PlayerKeys(player, "right", false);
        }else{
            pk1 = new PlayerKeys(player, "right", false);
            pk2 = new PlayerKeys(player, "left", false);
        }
        playerKeysThread1 = new Thread(pk1);
		playerKeysThread2 = new Thread(pk2);
		playerKeysThread1.start();
		playerKeysThread2.start();

        // Inicializa la comunicación con el servidor
        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(con.getOutputStream()), true);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = false;
        } else if (keyCode == keyUp) {
            movingUp = false;
        } else if (keyCode == keyDown) {
            movingDown = false;
        } else if (keyCode == keyLeft) {
            movingLeft = false;
        } else if (keyCode == keyRight) {
            movingRight = false;
        }
        System.out.println(keyCode);
        sendMessageToServer("keyReleased:" + keyCode);
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = true;
        } else if (keyCode == keyUp) {
            movingUp = true;
        } else if (keyCode == keyDown) {
            movingDown = true;
        } else if (keyCode == keyLeft) {
            movingLeft = true;
        } else if (keyCode == keyRight) {
            movingRight = true;
        }

        if (keyCode == keyShoot) {
            player.shoot();
        }
        System.out.println(keyCode);
        sendMessageToServer("keyPressed:" + keyCode);
    }

    private void sendMessageToServer(String message) {
        System.out.println("Mensaje enviado: " + message);
        out.println(message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String response = in.readLine();
                if (response != null) {
                    System.out.println("Mensaje recibido del servidor: " + response);
                    handleServerResponse(response);
                } else {
                    System.out.println("El servidor cerró la conexión.");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10); // Adjust sleep time as needed
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleServerResponse(String response) {
        int oldPositionX;
        int oldPositionY;
        String[] parts = response.split(":");
        if (parts.length == 3) {
            String playerIdentifier = parts[0];
            String action = parts[1];
            int keyCode = Integer.parseInt(parts[2]);

            if (imPlayer1 && playerIdentifier.equals("Player1")) {
                if (action.equals("keyPressed")) {
                    oldPositionX = player.getPlayerX();
                    oldPositionY = player.getPlayerY();
                    pk2.processKeyPressed(keyCode);
                    if ((player.getPlayerX()) - oldPositionX > 2) {
                         System.out.println("Hay problemas de conexión, corrigiendo error...");
                         player.setPlayerX(oldPositionX+2);
                    }
                    if ((player.getPlayerY()) - oldPositionY > 2) {
                        System.out.println("Hay problemas de conexión, corrigiendo error...");
                        player.setPlayerY(oldPositionY+2);
                   }
                } else if (action.equals("keyReleased")) {
                    pk1.processKeyReleased(keyCode);
                }
            }else if(!imPlayer1 && playerIdentifier.equals("Player2")) {
                if (action.equals("keyPressed")) {
                    oldPositionX = player.getPlayerX();
                    oldPositionY = player.getPlayerY();
                    pk2.processKeyPressed(keyCode);
                    if ((player.getPlayerX()) - oldPositionX > 2) {
                         System.out.println("Hay problemas de conexión, corrigiendo error...");
                         player.setPlayerX(oldPositionX+2);
                    }
                    if ((player.getPlayerY()) - oldPositionY > 2) {
                        System.out.println("Hay problemas de conexión, corrigiendo error...");
                        player.setPlayerY(oldPositionY+2);
                   }
                } else if (action.equals("keyReleased")) {
                    pk2.processKeyReleased(keyCode);
                }
            }
        }
    }
}
