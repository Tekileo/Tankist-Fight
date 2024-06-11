import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
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
              // Load assetsbg.png from the classpath
        BufferedImage assetsbgImage = ImageIO.read(getClass().getResource("assetsbg.png"));
        // Load barricadeWood.png from the classpath
        BufferedImage barricadeWoodImage = ImageIO.read(getClass().getResource("barricadeWood.png"));
        // Load barricadeMetal.png from the classpath
        BufferedImage barricadeMetalImage = ImageIO.read(getClass().getResource("barricadeMetal.png"));
            int iconIndex = 0;
            for (int y = 0; y < 200; y += 50) {
                for (int x = 0; x < 500; x += 50) {
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
                // g.setColor(Color.RED);
                // g.drawRect(x, y, 50, 50); 
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

    // Confirm collision with breakable bricks and remove them
    public boolean tankCollision(int x, int y) {
        // Calculate the row and column indices from x and y positions
        int row = y / 50;
        int col = x / 50;
        // Check if the brick at the calculated indices is breakable
        if (assetsMatrix[row][col] == 41) {
            // Define the reduced width and height for collision with element 41
            int reducedWidth = 30;
            int reducedHeight = 30;
            
            // Calculate the center position of the brick
            int brickCenterX = col * 50 + 25;
            int brickCenterY = row * 50 + 25;
            
            // Calculate the top-left corner of the reduced collision box
            int topLeftX = brickCenterX - reducedWidth / 2;
            int topLeftY = brickCenterY - reducedHeight / 2;
            
            // Check if the tank's center is within the reduced collision box
            int tankCenterX = x + 30; // Assuming the tank image size is 30x30
            int tankCenterY = y + 30;
            
            boolean withinXRange = tankCenterX >= topLeftX && tankCenterX <= topLeftX + reducedWidth;
            boolean withinYRange = tankCenterY >= topLeftY && tankCenterY <= topLeftY + reducedHeight;
            
            assetsMatrix[row][col] = 0;
            return withinXRange && withinYRange;
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
    private static int[][] assetsMatrix = {
        {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
        {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
        {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20},
        {17, 17, 39, 17, 17, 17, 17, 17, 17, 17, 39, 17, 17},
        {0, 41, 1, 0, 0, 0, 41, 41, 0, 42, 1, 0, 0},
        {0, 0, 1, 0, 0, 0, 41, 0, 41, 42, 1, 0, 0},
        {0, 0, 3, 2, 2, 2, 2, 2, 2, 2, 4, 0, 0},
        {0, 0, 1, 41, 41, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 15, 2, 14, 0, 42, 0, 13, 2, 16, 0, 0},
        {0, 0, 0, 0, 1, 41, 42, 41, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 41, 42, 41, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 41, 42, 41, 1, 0, 0, 0, 0}
    };

    // Get the value from assetsMatrix with corrected indices
    public static int getAssetsMatrix(int x, int y) {
        if (x >= 0 && x < assetsMatrix.length && y >= 0 && y < assetsMatrix[0].length) {
            return assetsMatrix[y][x]; // Access the array with the corrected indices
        } else {
            return 0; // Return 0 for out-of-bounds coordinates
        }
    }
}
