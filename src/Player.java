import java.awt.Graphics;

import javax.swing.ImageIcon;
import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private Graphics g;
    private int id;
    private ImageIcon player;
    private transient PlayerBullet pb = new PlayerBullet();
    private int playerColor;
    private String playerColorString;
    private int playerX = 0;
    private int playerY = 0;
    private boolean right = false;
    private boolean left = false;
    private boolean down = false;
    private boolean up = true;
    private int playerScore = 0;
    private int playerLives = 0;
    private String facing;
    private boolean playerShot = false;
    private String bulletShootDir = "";
    private int initialX;
    private String name;
    private int initialY;
    private static boolean firstRun = true;

    public Player(int playerX, int playerY, String playerColor, int playerLives, String name) {
        this.name = name;
        this.playerX = playerX;
        this.playerY = playerY;
        this.initialX = playerX;
        this.initialY = playerY;
        this.playerColorString = playerColor;
        switch (playerColor) {
            case "green":
                this.playerColor = 1;
                break;
            case "orange":
                this.playerColor = 2;
                break;
            default:
                break;
        }
        this.playerLives = playerLives;

    }

    public ImageIcon getPlayer() {
        return player;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerPos(int x, int y){
        this.playerX = x;
        this.playerY = y;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void resetPlayerPosition(){
        this.playerX = this.initialX;
        this.playerY = this.initialY;
    }

    public void setPlayer() {
        if(up){
            this.player = new ImageIcon("player"+playerColor+"_tank_up.png");	
        // System.out.println("Player is set facing UP"+"\n"+"Color: "+playerColor);                
        }
        else if(down){
            this.player = new ImageIcon("player"+playerColor+"_tank_down.png");	
        // System.out.println("Player is set facing DOWN"+"\n"+"Color: "+playerColor);          
        }
        else if(right){
            this.player = new ImageIcon("player"+playerColor+"_tank_right.png");	
                // System.out.println("Player is set facing RIGHT"+"\n"+"Color: "+playerColor);
        }
        else if(left){
            this.player = new ImageIcon("player"+playerColor+"_tank_left.png");	
                // System.out.println("Player is set facing LEFT"+"\n"+"Color: "+playerColor);
        }

    }

    public String getPlayerColor() {
        return playerColorString;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColorString = playerColor;
    }

    public int getPlayerX() {
        return playerX;
    }

    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerY(int playerY) {
        this.playerY = playerY;
    }

    public boolean isPlayerRight() {
        return right;
    }

    public void setPlayerright(boolean player1right) {
        this.right = player1right;
    }

    public boolean isPlayerLeft() {
        return left;
    }

    public void setPlayerLeft(boolean player1left) {
        this.left = player1left;
    }

    public boolean isPlayerdown() {
        return down;
    }

    public void setPlayerdown(boolean player1down) {
        this.down = player1down;
    }

    public boolean isPlayerup() {
        return up;
    }

    public void setPlayerup(boolean player1up) {
        this.up = player1up;
    }

    public int getScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getLives() {
        return playerLives;
    }

    public void setPlayerLives(int playerLives) {
        this.playerLives = playerLives;
    }

    public boolean playerShot() {
        return playerShot;
    }

    public void setPlayerShot(boolean playerShoot) {
        this.playerShot = playerShoot;
    }

    public String getBulletShootDir() {
        return bulletShootDir;
    }

    public void setBulletShootDir() {
        this.bulletShootDir = facing;
    }

    public void setBulletShootDir(String position) {
        this.bulletShootDir = position;
    }


    public void removeLive(int i) {
        this.playerLives -= i;
    }

    public void addScore(int score) {
        this.playerScore += score;
    }

    public void setFacingPosition(String position) {
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        this.facing = position;

        switch (position) {
            case "up":
                this.up = true;
                break;
            case "down":
                this.down = true;
                break;
            case "left":
                this.left = true;
                break;
            case "right":
                this.right = true;
                break;
            default:
                break;
        }
    }



    public void modifyPlayerY(int newY) {
        this.playerY += newY;
    }

    public void modifyPlayerX(int newX) {
        this.playerX += newX;
    }


    public String getFacingPosition() {
        System.out.println("[right = "+this.right+"\n"+ "Left = "+this.left+"\n"+ "Up = "+this.up+"\n"+ "Down = "+this.down+"]");
        return facing; 
    
    }

    public void setInitialPosition(String string) {
        if (firstRun == true){
            setFacingPosition(string);

        }
        firstRun = false; 
    }

    public void shoot() {
        getFacingPosition();
        if(!playerShot)
				{
					if(getFacingPosition().equals("up"))
					{					
						pb = new PlayerBullet(playerX + 20, playerY);
					}
					else if(getFacingPosition().equals("down"))
					{					
						pb = new PlayerBullet(playerX + 20, playerY + 40);
					}
					else if(getFacingPosition().equals("right"))
					{				
						pb = new PlayerBullet(playerX + 40, playerY + 20);
					}
					else if(getFacingPosition().equals("left"))
					{			
						pb = new PlayerBullet(playerX, playerY + 20);
					}
					
					setPlayerShot(true);
				}
                pb.move(getBulletShootDir());
                pb.draw(g);
    }

    public void setBulletGraphic(Graphics g2) {
        g = g2;
    }

    public PlayerBullet getBullet(){
        return pb;
    }

    public int getBulletX() {
    	if (pb == null) {
    		return 0;
    	}else {
    		return pb.getX();
    	}
    }

    public int getBulletY() {
    	if(pb == null) {
    		return 0;
    	}else {
    		return pb.getY();
    		
    	}
    }

    public void delBullet() {
        pb = null;
    }

    public String getName() {
        return this.name;
    }

    public boolean canMove(int x, int y) {
        
        return ObjectsCollide.getAssetsMatrix(x,y) != 42;

    }

    public void resetMove() {
        this.left = false;
        this.right = false;
        this.up = false;
        this.down = false;
    }

}
