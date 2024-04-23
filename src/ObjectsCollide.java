import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ObjectsCollide {

    // ImageIcons for breakable and solid bricks
    private ImageIcon[] brickIcons = new ImageIcon[40];
    private ImageIcon barricadeWoodIcon;
    private ImageIcon barricadeMetalIcon;

    // Constructor
    public ObjectsCollide() {
        // Constructor initializes the Images
        loadBrickIcons();
    }

    // Load breakable and solid brick icons from the cropped assets
    private void loadBrickIcons() {
        try {
            BufferedImage assetsbgImage = ImageIO.read(new File("assetsbg.png"));
            BufferedImage barricadeWoodImage = ImageIO.read(new File("barricadeWood.png"));
            BufferedImage barricadeMetalImage = ImageIO.read(new File("barricadeMetal.png"));
            int iconIndex = 0;
            for (int y = 0; y < 200; y += 50) {
                for (int x = 0; x < 200; x += 50) {
                    if (iconIndex < brickIcons.length) {
                        BufferedImage iconImage = assetsbgImage.getSubimage(x, y, 50, 50);
                        brickIcons[iconIndex++] = new ImageIcon(iconImage);
                    }
                }
            }
            barricadeWoodIcon = new ImageIcon(barricadeWoodImage);
            barricadeMetalIcon = new ImageIcon(barricadeMetalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Draw method for breakable and solid bricks
    public void draw(Component c, Graphics g) {
        // Draw breakable and solid bricks based on brickMatrix
        for (int i = 0; i < assetsMatrix.length; i++) {
            for (int j = 0; j < assetsMatrix[i].length; j++) {
                int x = j * 50; // Calculate x-coordinate
                int y = i * 50; // Calculate y-coordinate
                int brickType = assetsMatrix[i][j];
                brickIcons[0].paintIcon(c, g, x, y); // Use the first image from brickIcons for all tiles
                if (brickType == 41) {
                    barricadeWoodIcon.paintIcon(c, g, x + 12, y + 12); // Draw barricadeWood centered in the tile
                } else if (brickType == 42) {
                    barricadeMetalIcon.paintIcon(c, g, x + 12, y + 12); // Draw barricadeMetal centered in the tile
                }else if (brickType > 0 && brickType < brickIcons.length) {
                    brickIcons[brickType].paintIcon(c, g, x, y);
                }
            }
        }
    }

    // Check for collision with breakable bricks
    public boolean checkCollision(int x, int y) {
        // Calculate the row and column indices from x and y positions
        int row = y / 50;
        int col = x / 50;
        // Check if the brick at the calculated indices is breakable
        if (assetsMatrix[row][col] == 41) {
            assetsMatrix[row][col] = 0; // Mark brick as broken
            return true;
        }
        return false;
    }

    // Check for collision with solid bricks
    public boolean checkSolidCollision(int x, int y) {
        // Calculate the row and column indices from x and y positions
        int row = y / 50;
        int col = x / 50;
        // Check if the brick at the calculated indices is solid
        return assetsMatrix[row][col] == 42;
    }

    // Define the matrix to represent the brick layout
    private int[][] assetsMatrix = {
        {0, 0, 0, 0, 42, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 41, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}
    };

}
