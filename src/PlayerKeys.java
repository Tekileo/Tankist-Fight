import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerKeys implements KeyListener, Runnable {
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

    public PlayerKeys(Player player, String side) {
        this.player = player;
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
        }
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }

    @Override
    public void run() {
        while (true) {
            if (movingUp && canMoveUp()) {
                player.setFacingPosition("up");
                player.modifyPlayerY(-MOVE_SPEED);
            }
            if (movingDown && canMoveDown()) {
                player.setFacingPosition("down");
                player.modifyPlayerY(MOVE_SPEED);
            }
            if (movingLeft && canMoveLeft()) {
                player.setFacingPosition("left");
                player.modifyPlayerX(-MOVE_SPEED);
            }
            if (movingRight && canMoveRight()) {
                player.setFacingPosition("right");
                player.modifyPlayerX(MOVE_SPEED);
            }
            try {
                Thread.sleep(10); // Adjust sleep time as needed
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

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
}
