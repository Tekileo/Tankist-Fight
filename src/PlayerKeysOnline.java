import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

public class PlayerKeysOnline implements KeyListener, Runnable {
    private Player player;
    private int keyUp;
    private int keyDown;
    private int keyLeft;
    private int keyRight;
    private int keyShoot;
    private boolean spacePressed = false;
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private static final int MOVE_SPEED = 2;
    private Socket socket;
    private ObjectOutputStream outputStream;

    public PlayerKeysOnline(Player player, Socket socket, String side) {
        this.player = player;
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (side) {
            case "left":
                keyUp = KeyEvent.VK_W;
                keyDown = KeyEvent.VK_S;
                keyLeft = KeyEvent.VK_A;
                keyRight = KeyEvent.VK_D;
                keyShoot = KeyEvent.VK_U;
                break;
            case "right":
                keyUp = KeyEvent.VK_UP;
                keyDown = KeyEvent.VK_DOWN;
                keyLeft = KeyEvent.VK_LEFT;
                keyRight = KeyEvent.VK_RIGHT;
                keyShoot = KeyEvent.VK_M;
                break;
            default:
                break;
        }
    }


    // Method to send the pressed key to the other player
   private void sendPlayerKey(int keyCode) {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String keyString = getKeyString(keyCode);
            if (keyString != null) {
                writer.println(keyString);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPlayerKey(String key) {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String keyString = key;
            if (keyString != null) {
                writer.println(keyString);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getKeyString(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
                return "W";
            case KeyEvent.VK_S:
                return "S";
            case KeyEvent.VK_A:
                return "A";
            case KeyEvent.VK_D:
                return "D";
            // Add more cases for other key codes if needed
            default:
                return null;
        }
    }

     private static class KeyMessage implements Serializable {
        private static final long serialVersionUID = 1L;
        private int keyCode;

        public KeyMessage(int keyCode) {
            this.keyCode = keyCode;
        }

        public int getKeyCode() {
            return keyCode;
        }
    }

    public void keyTyped(KeyEvent e) {}

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
            sendPlayerKey("U");
        }

    }

    // Implement the KeyListener interface methods
    // ...


    private boolean canMoveUp() {
        int x = player.getPlayerX() / 50;
        int y = (player.getPlayerY() - MOVE_SPEED) / 50;
        return y >= 0 && player.getPlayerY() >= MOVE_SPEED && player.canMove(x, y);
    }

    private boolean canMoveDown() {
        int x = player.getPlayerX() / 50;
        int y = (player.getPlayerY() + 52 + MOVE_SPEED) / 50;
        return y < 12 && player.getPlayerY() <= 548 - MOVE_SPEED && player.canMove(x, y);
    }

    private boolean canMoveLeft() {
        int x = (player.getPlayerX() - MOVE_SPEED) / 50;
        int y = player.getPlayerY() / 50;
        return x >= 0 && player.getPlayerX() >= MOVE_SPEED && player.canMove(x, y);
    }

    private boolean canMoveRight() {
        int x = (player.getPlayerX() + 52 + MOVE_SPEED) / 50;
        int y = player.getPlayerY() / 50;
        return x < 13 && player.getPlayerX() <= 598 - MOVE_SPEED && player.canMove(x, y);
    }

    @Override
    public void run() {
      
        
            while (true) {
                if (movingUp && canMoveUp()) {
                    player.setFacingPosition("up");
                    player.modifyPlayerY(-MOVE_SPEED);
                    sendPlayerKey("W");
                }
                if (movingDown && canMoveDown()) {
                    player.setFacingPosition("down");
                    player.modifyPlayerY(MOVE_SPEED);
                    sendPlayerKey("S");
                }
                if (movingLeft && canMoveLeft()) {
                    player.setFacingPosition("left");
                    player.modifyPlayerX(-MOVE_SPEED);
                    sendPlayerKey("A");
                }
                if (movingRight && canMoveRight()) {
                    player.setFacingPosition("right");
                    player.modifyPlayerX(MOVE_SPEED);
                    sendPlayerKey("D");
                }



                try {
                    Thread.sleep(10); // Adjust sleep time as needed
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }


    public boolean isSpacePressed() {
        return spacePressed;
    }



    // Implement other necessary methods for online gameplay
    // ...
}
