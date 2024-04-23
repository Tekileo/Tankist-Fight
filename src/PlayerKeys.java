import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerKeys implements KeyListener {

    private Player player;
    private int keyUp;
    private int keyDown;
    private int keyLeft;
    private int keyRight;
    private int keyShoot;
    private boolean spacePressed = false;

    public PlayerKeys(Player player, String side) {
        super();
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

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }

        if (e.getKeyCode() == keyShoot) {
            player.shoot();
        }
        if (e.getKeyCode() == keyUp && canMoveUp()) {
            player.setFacingPosition("up");
            player.modifyPlayerY(-2);
        }
        if (e.getKeyCode() == keyLeft && canMoveLeft()) {
            player.setFacingPosition("left");
            player.modifyPlayerX(-2);
        }
        if (e.getKeyCode() == keyDown && canMoveDown()) {
            player.setFacingPosition("down");
            player.modifyPlayerY(2);
        }
        if (e.getKeyCode() == keyRight && canMoveRight()) {
            player.setFacingPosition("right");
            player.modifyPlayerX(2);
        }
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }

    private boolean canMoveUp() {
        int x = player.getPlayerX() / 50;
        int y = (player.getPlayerY() - 2) / 50;
        return y >= 0 && player.getPlayerY() >= 2 && player.canMove(x, y);
    }
    
    private boolean canMoveDown() {
        int x = player.getPlayerX() / 50;
        int y = (player.getPlayerY() + 52) / 50;
        return y < 12 && player.getPlayerY() <= 548 && player.canMove(x, y);
    }
    
    private boolean canMoveLeft() {
        int x = (player.getPlayerX() - 2) / 50;
        int y = player.getPlayerY() / 50;
        return x >= 0 && player.getPlayerX() >= 2 && player.canMove(x, y);
    }
    
    private boolean canMoveRight() {
        int x = (player.getPlayerX() + 52) / 50;
        int y = player.getPlayerY() / 50;
        return x < 13 && player.getPlayerX() <= 598 && player.canMove(x, y);
    }
    
}
