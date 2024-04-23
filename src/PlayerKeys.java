import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerKeys implements KeyListener{

    private Player player;
    private int keyUp;
    private int keyDown;
    private int keyLeft;
    private int keyRight;
    private int keyShoot;
    private boolean spacePressed = false;


    public PlayerKeys(Player player, String side){
        super();
        this.player = player;
        switch (side) {
            case "left" :
                keyUp =  KeyEvent.VK_W;
                keyDown =  KeyEvent.VK_S;
                keyLeft =  KeyEvent.VK_A;
                keyRight =  KeyEvent.VK_D;
                keyShoot = KeyEvent.VK_U;
                break;
            case "right" :
                keyUp =  KeyEvent.VK_UP;
                keyDown =  KeyEvent.VK_DOWN;
                keyLeft =  KeyEvent.VK_LEFT;
                keyRight =  KeyEvent.VK_RIGHT;
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

        if(e.getKeyCode()== keyShoot)
        {
            player.shoot();
        }
        if(e.getKeyCode()== keyUp)
        {
            player.setFacingPosition("up");	

            
            if(!(player.getPlayerX() < 0))
                player.modifyPlayerY(-2);

        }
        if(e.getKeyCode()== keyLeft)
        {
            player.setFacingPosition("left");
            System.out.println(player.getFacingPosition());
            
            if(!(player.getPlayerX() < 2))
                player.modifyPlayerX(-2);
        }
        if(e.getKeyCode()== keyDown)
        {
            player.setFacingPosition("down");
            System.out.println(player.getFacingPosition());
            
            if(!(player.getPlayerY() > 540))
                player.modifyPlayerY(2);
        }
        if(e.getKeyCode()== keyRight)
        {
            player.setFacingPosition("right");
            System.out.println(player.getFacingPosition());
            
            if(!(player.getPlayerX() > 600))
                player.modifyPlayerX(2);
        }
        
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }



}
