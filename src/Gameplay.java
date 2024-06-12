import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;

public class Gameplay extends JPanel implements ActionListener {

	private ObjectsCollide br = new ObjectsCollide();
	private Player player1;
	private Player player2;

	private ArrayList<Player> players = new ArrayList<>();
	private Timer timer;
	private int delay = 2;
	private int timesSpace = 0;

	private static final String DB_URL = "jdbc:mysql://limines.duckdns.org:3306/tankist";
	private static final String USER = "tankist";
	private static final String PASSWORD = "TankistFight24";

	private PlayerKeys pk1;
	private PlayerKeys pk2;
	private PlayerKeysOnline pko;
	private HandlerResponse phr;

	private Thread playerKeysOnlineThread;
	private Thread phrThread;

	private Thread playerKeysThread1;
	private Thread playerKeysThread2;

	private boolean play = true;
	private Socket con;
	private boolean online;
	private boolean isHost;
	private String name;
	private Clip backgroundMusicClip;

	public Gameplay(boolean online, Socket con, boolean isHost, String name) throws Exception {
		this.online = online;
		this.isHost = isHost;
		this.con = con;
		this.name = name;
		playBackgroundMusic("resources/BackgroundSound.wav",-10.5f);
		if (online) {
			player1 = new Player(200, 550, "green", 5, null);
			player2 = new Player(400, 550, "orange", 5, null);
			players.add(player1);
			players.add(player2);
			if (isHost) {
				player1.setName(name);
				player2.setName("Player 2");
				pko = new PlayerKeysOnline(player1, con, "left");
				phr = new HandlerResponse(player2, con, "Player2");
				System.out.println("Im the green one");
			} else {
				player2.setName(name);
				player1.setName("Player 1");
				pko = new PlayerKeysOnline(player2, con, "left");
				phr = new HandlerResponse(player1, con, "Player1");
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
			player1 = new Player(200, 550, "green", 5, "Player 1");
			player2 = new Player(400, 550, "orange", 5, "Player 2");
			pk1 = new PlayerKeys(player1, "left");
			pk2 = new PlayerKeys(player2, "right");

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
		g.fillRect(0, 0, 650, 635);

		// right side background
		g.setColor(Color.DARK_GRAY);
		g.fillRect(660, 0, 180, 635);

		// draw screen assets
		br.draw(this, g);

		// Set initial config of the players
		if (play) {
			// draw player and placing the facing of the player
			player1.setInitialPosition("up");
			player1.setPlayer();
			player1.setBulletGraphic(g);

			ImageIcon playerPainter1 = player1.getPlayer();
			playerPainter1.paintIcon(this, g, player1.getPlayerX(), player1.getPlayerY());

			// draw player 2
			player2.setPlayer();
			player2.setInitialPosition("up");
			player2.setBulletGraphic(g);

			ImageIcon playerPainter2 = player2.getPlayer();
			playerPainter2.paintIcon(this, g, player2.getPlayerX(), player2.getPlayerY());

			// Collition Tank with objects 42 and 41
			// Handle collision detection for player 1 with breakable and solid bricks
			if (br.tankCollision(player1.getPlayerX(), player1.getPlayerY())) {
				player1.removeLive(1); // Remove a life if colliding with breakable bricks
			}

			// Handle collision detection for player 2 with breakable and solid bricks
			if (br.tankCollision(player2.getPlayerX(), player2.getPlayerY())) {
				player2.removeLive(1); // Remove a life if colliding with breakable bricks
			}

			// Collition Bullet
			handleBulletCollision(g, player1, player2);
			handleBulletCollision(g, player2, player1);
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

		handleGameOver(g);
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

	private void handleBulletCollision(Graphics g, Player shooter, Player target) {
		if (shooter.getBullet() != null && shooter.playerShot()) {
			if (shooter.getBulletShootDir().equals("")) {
				shooter.setBulletShootDir();
			} else {
				shooter.shoot();
			}

			if (new Rectangle(shooter.getBulletX(), shooter.getBulletY(), 10, 10)
					.intersects(new Rectangle(target.getPlayerX(), target.getPlayerY(), 50, 50))) {
				shooter.addScore(10);
				target.removeLive(1);
				playShootSoundHit("car");
				shooter.delBullet();
				shooter.setPlayerShot(false);
				shooter.setBulletShootDir("");
			}

			if (br.checkCollision(shooter.getBulletX(), shooter.getBulletY())
					|| br.checkSolidCollision(shooter.getBulletX(), shooter.getBulletY())) {
				shooter.delBullet();
				playShootSoundHit("bar");
				shooter.setPlayerShot(false);
				shooter.setBulletShootDir("");
			}

			if (shooter.getBulletY() < 1 || shooter.getBulletY() > 580 || shooter.getBulletX() < 1
					|| shooter.getBulletX() > 630) {
				shooter.delBullet();
				shooter.setPlayerShot(false);
				shooter.setBulletShootDir("");
			}
		}
	}

	private void playBackgroundMusic(String filename, float volume) {
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

	private void playShootSoundHit(String obj) {
		try {
			File audioFile;
			// Load the audio file
			switch (obj) {
				case "bar":
					audioFile = new File("resources/HitBar.wav");

					break;
				case "car":

					audioFile = new File("resources/Hit.wav");

					break;
				default:
					audioFile = new File("resources/Hit.wav");
					break;
			}
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

			// Get a Clip object to play the audio
			Clip clip = AudioSystem.getClip();

			// Open the audio stream
			clip.open(audioStream);

			// Start playing the audio
			clip.start();
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
			ex.printStackTrace();
		}
	}

	private void handleGameOver(Graphics g) {
		if (player1.getLives() == 0) {
			drawGameOver(g, player2.getName());
			play = false;
		} else if (player2.getLives() == 0) {
			drawGameOver(g, player1.getName());
			play = false;
		}

		if (!online) {
			if (player1.getLives() == 0 || player2.getLives() == 0) {
				if (pk1.isSpacePressed() || pk2.isSpacePressed()) {
					resetGame();
				}
			}
		} else {
			if ((player1.getLives() == 0 || player2.getLives() == 0) && isHost) {
				if (pko.isSpacePressed()) {
					timesSpace += 1;
					if (timesSpace == 1) {
						updateDatabase();
						restartApplication();
					}
				}
			}
			if ((player1.getLives() == 0 || player2.getLives() == 0) && !isHost) {
				if (pko.isSpacePressed()) {
					timesSpace += 1;
					if (timesSpace == 1) {
						restartApplication();
					}
				}
			}
		}
	}

	private void drawGameOver(Graphics g, String winnerName) {
		g.setColor(Color.white);
		g.setFont(new Font("HELVETICA", Font.BOLD, 60));
		g.drawString("Game Over", 200, 300);
		g.drawString(winnerName + " Won", 180, 380);
		g.setFont(new Font("HELVETICA", Font.BOLD, 30));
		if (online) {
			g.drawString("(Space to close)", 230, 430);
		} else {
			g.drawString("(Space to go back)", 230, 430);

		}
	}

	private void resetGame() {
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

	private void updateDatabase() {
		try {
			// Calculate the total score for updating the database
			int matchScore = player1.getScore() + player2.getScore();

			// Establish database connection
			Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

			// Update Player1's score in the database
			updatePlayerScore(connection, player1.getName(), player1.getScore());

			// Update Player2's score in the database
			updatePlayerScore(connection, player2.getName(), player2.getScore());

			// Prepare the SQL statement to insert into PlayersTog
			String sql = "INSERT INTO PlayersTog(Mapa, Player1, Player2, Score) VALUES (?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);

			// Set the values for the parameters in the prepared statement
			statement.setInt(1, 1); // Mapa value
			statement.setString(2, player1.getName()); // Player1 name
			statement.setString(3, player2.getName()); // Player2 name
			statement.setInt(4, matchScore); // Total match score

			// Execute the statement to insert the new entry into the database
			statement.executeUpdate();

			// Close the statement and connection
			statement.close();
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updatePlayerScore(Connection connection, String playerName, int matchScore) throws SQLException {
		// Prepare the SQL statement to update the player's score
		String sql = "UPDATE Player SET Score = Score + ? WHERE Name = ?";
		PreparedStatement statement = connection.prepareStatement(sql);

		// Set the values for the parameters in the prepared statement
		statement.setInt(1, matchScore); // Match score
		statement.setString(2, playerName); // Player name

		// Execute the statement to update the player's score in the database
		statement.executeUpdate();

		// Close the statement
		statement.close();
	}

	private void restartApplication() {
		try {
			String javaBin = System.getProperty("java.home") + "/bin/java";
			String jarPath = new File(Gameplay.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getPath();

			ArrayList<String> command = new ArrayList<>();
			command.add(javaBin);
			command.add("-jar");
			command.add(jarPath);

			ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
