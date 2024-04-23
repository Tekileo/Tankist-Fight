import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;


public class Gameplay  extends JPanel implements ActionListener 
{
	private ObjectsCollide br;

	private Player player1 = new Player(200, 550, "green", 5, "Tekileo");
	private Player player2 = new Player(400, 550, "orange", 5, "Naranjito");
	
	private Timer timer;
	private int delay=2;
	
	private PlayerKeys pk1;
	private PlayerKeys pk2;
	
	
	private boolean play = true;
	
	public Gameplay()
	{				
		br = new ObjectsCollide();
		pk1 = new PlayerKeys(player1, "left");
		pk2 = new PlayerKeys(player2, "right");
		setFocusable(true);
		addKeyListener(pk1);
		addKeyListener(pk2);
		setFocusTraversalKeysEnabled(false);
        timer=new Timer(delay,this);
		timer.start();
	}
	
	public void paint(Graphics g)
	{    		
		// play background
		g.setColor(Color.green);
		g.fillRect(0, 0, 650, 600);
		
		// right side background
		g.setColor(Color.DARK_GRAY);
		g.fillRect(660, 0, 180, 600);
		
		// draw screen assets
		br.draw(this, g);

		//Set initial config of the players
		
		if(play)
		{
			// draw player and placing the facing of the player
			
			player1.setInitialPosition("up");
			player1.setPlayer();
			player1.setBulletGraphic(g);
		
			//!TODO: See how can I refactor this code and get it to work properly
			ImageIcon playerPainter1 = player1.getPlayer();
			playerPainter1.paintIcon(this, g, player1.getPlayerX(), player1.getPlayerY());
			// player1.paintIcon(this, g, player1.getPlayerX(), player1.getPlayerY());

			// draw player 2
			player2.setPlayer();
			player2.setInitialPosition("up");
			player2.setBulletGraphic(g);
						
			ImageIcon playerPainter2 = player2.getPlayer();
			playerPainter2.paintIcon(this, g, player2.getPlayerX(), player2.getPlayerY());
			
			//Collition Tank with objects 42 and 41
			 // Handle collision detection for player 1 with breakable and solid bricks
			 if (ObjectsCollide.getAssetsMatrix(player1.getPlayerX(), player1.getPlayerY()) == 41) {
				System.out.println(ObjectsCollide.getAssetsMatrix(player1.getPlayerX(), player1.getPlayerY()));
				if (br.confirmCollision(player1.getPlayerX(), player1.getPlayerY())) {
					player1.modifyPlayerX(0); // Reset player position if colliding with breakable bricks
					player1.removeLive(1); // Remove a life if colliding with breakable bricks
				}
			}
	
	
			// Handle collision detection for player 2 with breakable and solid bricks
			if (br.checkCollision(player2.getPlayerX(), player2.getPlayerY())) {
				player2.modifyPlayerX(0); // Reset player position if colliding with breakable bricks
				player2.removeLive(1); // Remove a life if colliding with breakable bricks
			}
	

			//Collition Bullet
			if(player1.getBullet() != null && player1.playerShot())
			{
				if(player1.getBulletShootDir().equals(""))
				{
					player1.setBulletShootDir();
				}
				else
				{
					player1.shoot();
				}
				
				
				if(new Rectangle(player1.getBulletX(), player1.getBulletY(), 10, 10)
				.intersects(new Rectangle(player2.getPlayerX(), player2.getPlayerY(), 50, 50)))
				{
					player1.addScore(10);
					player2.removeLive(1); 
					player1.delBullet();
					player1.setPlayerShot(false);
					player1.setBulletShootDir("");
				}
				
				if(br.checkCollision(player1.getBulletX(), player1.getBulletY())
						|| br.checkSolidCollision(player1.getBulletX(), player1.getBulletY()))
				{
					player1.delBullet();
					player1.setPlayerShot(false);
					player1.setBulletShootDir("");			
				}
	
				if(player1.getBulletY() < 1 
						|| player1.getBulletY() > 580
						|| player1.getBulletX() < 1
						|| player1.getBulletX() > 630)
				{
					player1.delBullet();
					player1.setPlayerShot(false);
					player1.setBulletShootDir("");
				}
			}
			
			if(player2.getBullet() != null && player2.playerShot())
			{
				if(player2.getBulletShootDir().equals(""))
				{
					player2.setBulletShootDir();
				}
				else
				{
					player2.shoot();
				}
				
				
				
				if(new Rectangle(player2.getBulletX(), player2.getBulletY(), 10, 10)
				.intersects(new Rectangle(player1.getPlayerX(), player1.getPlayerY(), 50, 50)))
				{
					player2.addScore(10);
					player1.removeLive(1);
					player2.delBullet();
					player2.setPlayerShot(false);
					player2.setBulletShootDir("");
				}
				
				if(br.checkCollision(player2.getBulletX(), player2.getBulletY())
						|| br.checkSolidCollision(player2.getBulletX(), player2.getBulletY()))
				{
					player2.delBullet();
					player2.setPlayerShot(false);
					player2.setBulletShootDir("");			
				}
				
				if(player2.getBulletY() < 1 
						|| player2.getBulletY() > 580
						|| player2.getBulletX() < 1
						|| player2.getBulletX() > 630)
				{
					player2.delBullet();
					player2.setPlayerShot(false);
					player2.setBulletShootDir("");
				}
			}
		}
	
		
		// the scores 		
		g.setColor(Color.white);
		g.setFont(new Font("HELVETICA",Font.BOLD, 15));
		g.drawString("Scores", 700,30);
		g.drawString("Player 1:  "+player1.getScore(), 670,60);
		g.drawString("Player 2:  "+player2.getScore(), 670,90);
		
		g.drawString("Lives", 700,150);
		g.drawString("Player 1:  "+player1.getLives(), 670,180);
		g.drawString("Player 2:  "+player2.getLives(), 670,210);
		
		if(player1.getLives() == 0)
		{
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA",Font.BOLD, 60));
			g.drawString("Game Over", 200,300);
			g.drawString(player2.getName()+" Won", 180,380);
			play = false;
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA",Font.BOLD, 30));
			g.drawString("(Space to Restart)", 230,430);
		}
		else if(player2.getLives() == 0)
		{
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA",Font.BOLD, 60));
			g.drawString("Game Over", 200,300);
			g.drawString(player1.getName()+" Won", 180,380);
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA",Font.BOLD, 30));
			g.drawString("(Space to Restart)", 230,430);
			play = false;
		}

		if(player1.getLives() == 0 || player2.getLives() == 0){
			if(pk1.isSpacePressed() || pk2.isSpacePressed()) {
				// Reset game state
				br = new ObjectsCollide();
				player1.resetPlayerPosition();
				player1.setFacingPosition("up");
				
				player2.resetPlayerPosition();
				player2.setFacingPosition("up");
				
				player1.setPlayerScore(0);
				player1.setPlayerLives(5);
				player2.setPlayerScore(0);
				player2.setPlayerLives(5);
				play = true;
				repaint();
			}
		}
		
		
		g.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		timer.start();
		
		repaint();
	}

	
	
}
