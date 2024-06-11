import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;

public class Gameplay extends JPanel implements ActionListener

{
	private ObjectsCollide br = new ObjectsCollide();
	private Player player1;
	private Player player2;

	private ArrayList<Player> players = new ArrayList<>();
	private Timer timer;
	private int delay = 2;

	private PlayerKeys pk1;
	private PlayerKeys pk2;
	private PlayerKeysOnline pko;
	private HandlerResponse phr;

	private Thread playerKeysOnlineThread;
	private Thread phrThread;

	private Thread playerKeysThread1;
	private Thread playerKeysThread2;
	// private Thread shotThread;

	private boolean play = true;
	private Socket con;
	private boolean online;
	


	public Gameplay(boolean online, Socket con, boolean isHost) throws Exception {
		this.online = online;
		this.con = con;
		if (online) {
            player1 = new Player(200, 550, "green", 5, "Tekileo");
            player2 = new Player(400, 550, "orange", 5, "Naranjito");
			players.add(player1);
			players.add(player2);
			if (isHost) {
				pko = new PlayerKeysOnline(player1 , con , "left");
				phr = new HandlerResponse(player2, con, "Player2");
				System.out.println("Im the green one");
			}else{
				pko = new PlayerKeysOnline(player2 , con, "left");
				phr = new HandlerResponse(player1, con,"Player1");
				System.out.println("Im the orange one");
			}
			
			addKeyListener(pko);
			
			playerKeysOnlineThread = new Thread(pko);
			phrThread = new Thread(phr);

			playerKeysOnlineThread.start();
			phrThread.start();

			br = new ObjectsCollide();
			


			// Make sure the JPanel or JFrame can receive focus
			this.setFocusable(true);
			this.requestFocusInWindow();
			timer = new Timer(delay, this);
			timer.start();
        } else {
            player1 = new Player(200, 550, "green", 5, "Tekileo");
			player2 = new Player(400, 550, "orange", 5, "Naranjito");
			pk1 = new PlayerKeys(player1, "left");
			pk2 = new PlayerKeys(player2, "left");

			playerKeysThread1 = new Thread(pk1);
			playerKeysThread2 = new Thread(pk2);
			playerKeysThread1.start();
			playerKeysThread2.start();
			br = new ObjectsCollide();
		
			setFocusable(true);
			addKeyListener(pk1);
			addKeyListener(pk2);
			setFocusTraversalKeysEnabled(false);
			timer = new Timer(delay, this);
			timer.start();
			}
        }
	
	public void paint(Graphics g) {
		// play background
		g.setColor(Color.green);
		g.fillRect(0, 0, 650, 600);

		// right side background
		g.setColor(Color.DARK_GRAY);
		g.fillRect(660, 0, 180, 600);

		// draw screen assets
		br.draw(this, g);

		// Set initial config of the players

		if (play) {
			// draw player and placing the facing of the player

			player1.setInitialPosition("up");
			player1.setPlayer();
			player1.setBulletGraphic(g);

			// !TODO: See how can I refactor this code and get it to work properly
			ImageIcon playerPainter1 = player1.getPlayer();
			playerPainter1.paintIcon(this, g, player1.getPlayerX(), player1.getPlayerY());
			// player1.paintIcon(this, g, player1.getPlayerX(), player1.getPlayerY());

			// draw player 2
			player2.setPlayer();
			// g.setColor(Color.RED);
			// int player1HitboxSize = 30;
			// g.drawRect(player1.getPlayerX() + (50 - player1HitboxSize) / 2,
			// player1.getPlayerY() + (50 - player1HitboxSize) / 2, player1HitboxSize,
			// player1HitboxSize);
			player2.setInitialPosition("up");
			player2.setBulletGraphic(g);

			ImageIcon playerPainter2 = player2.getPlayer();
			playerPainter2.paintIcon(this, g, player2.getPlayerX(), player2.getPlayerY());

			// Collition Tank with objects 42 and 41
			// Handle collision detection for player 1 with breakable and solid bricks
			if (br.tankCollision(player1.getPlayerX(), player1.getPlayerY())) {
				// player1.modifyPlayerX(0); // Reset player position if colliding with
				// breakable bricks
				player1.removeLive(1); // Remove a life if colliding with breakable bricks
			}

			// Handle collision detection for player 2 with breakable and solid bricks
			if (br.tankCollision(player2.getPlayerX(), player2.getPlayerY())) {
				// player2.modifyPlayerX(0); // Reset player position if colliding with
				// breakable bricks
				player2.removeLive(1); // Remove a life if colliding with breakable bricks
			}

			// Collition Bullet
			if (player1.getBullet() != null && player1.playerShot()) {
				if (player1.getBulletShootDir().equals("")) {
					player1.setBulletShootDir();
				} else {
					player1.shoot();
				}

				if (new Rectangle(player1.getBulletX(), player1.getBulletY(), 10, 10)
						.intersects(new Rectangle(player2.getPlayerX(), player2.getPlayerY(), 50, 50))) {
					player1.addScore(10);
					player2.removeLive(1);
					player1.delBullet();
					player1.setPlayerShot(false);
					player1.setBulletShootDir("");
				}

				if (br.checkCollision(player1.getBulletX(), player1.getBulletY())
						|| br.checkSolidCollision(player1.getBulletX(), player1.getBulletY())) {
					player1.delBullet();
					player1.setPlayerShot(false);
					player1.setBulletShootDir("");
				}

				if (player1.getBulletY() < 1
						|| player1.getBulletY() > 580
						|| player1.getBulletX() < 1
						|| player1.getBulletX() > 630) {
					player1.delBullet();
					player1.setPlayerShot(false);
					player1.setBulletShootDir("");
				}
			}

			if (player2.getBullet() != null && player2.playerShot()) {
				if (player2.getBulletShootDir().equals("")) {
					player2.setBulletShootDir();
				} else {
					player2.shoot();
				}

				if (new Rectangle(player2.getBulletX(), player2.getBulletY(), 10, 10)
						.intersects(new Rectangle(player1.getPlayerX(), player1.getPlayerY(), 50, 50))) {
					player2.addScore(10);
					player1.removeLive(1);
					player2.delBullet();
					player2.setPlayerShot(false);
					player2.setBulletShootDir("");
				}

				if (br.checkCollision(player2.getBulletX(), player2.getBulletY())
						|| br.checkSolidCollision(player2.getBulletX(), player2.getBulletY())) {
					player2.delBullet();
					player2.setPlayerShot(false);
					player2.setBulletShootDir("");
				}

				if (player2.getBulletY() < 1
						|| player2.getBulletY() > 580
						|| player2.getBulletX() < 1
						|| player2.getBulletX() > 630) {
					player2.delBullet();
					player2.setPlayerShot(false);
					player2.setBulletShootDir("");
				}
			}
		}

		// the scores
		g.setColor(Color.white);
		g.setFont(new Font("HELVETICA", Font.BOLD, 15));
		g.drawString("Scores", 700, 30);
		g.drawString(player1.getName() + ":  " + player1.getScore(), 670, 60);
		g.drawString(player2.getName() + ":  " + player2.getScore(), 670, 90);

		g.drawString("Lives", 700, 150);
		g.drawString(player1.getName() + ":  " + player1.getLives(), 670, 180);
		g.drawString(player2.getName() + ":  " + player2.getLives(), 670, 210);

		if (player1.getLives() == 0) {
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA", Font.BOLD, 60));
			g.drawString("Game Over", 200, 300);
			g.drawString(player2.getName() + " Won", 180, 380);
			play = false;
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA", Font.BOLD, 30));
			g.drawString("(Space to Restart)", 230, 430);
		} else if (player2.getLives() == 0) {
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA", Font.BOLD, 60));
			g.drawString("Game Over", 200, 300);
			g.drawString(player1.getName() + " Won", 180, 380);
			g.setColor(Color.white);
			g.setFont(new Font("HELVETICA", Font.BOLD, 30));
			g.drawString("(Space to Restart)", 230, 430);
			play = false;
		}

		if (player1.getLives() == 0 || player2.getLives() == 0) {
			if (pk1.isSpacePressed() || pk2.isSpacePressed()) {
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

	public void setSocket(Socket socket) {
	this.con = socket;
	}

}
