import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Leaderboard {

    private static final String DB_URL = "jdbc:mysql://limines.duckdns.org:3306/tankist";
    private static final String USER = "tankist";
    private static final String PASSWORD = "TankistFight24";

    public static void showLeaderboard(JFrame previousFrame) {
        try {
            updateLeaderboard();

            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement statement = connection.prepareStatement("SELECT Player, HighestScore FROM LeaderBoard ORDER BY HighestScore DESC LIMIT 10");
            ResultSet resultSet = statement.executeQuery();

            JTable table = new JTable(buildTableModel(resultSet)) {
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                renderer.setHorizontalAlignment(SwingConstants.CENTER); // Center align text
                return renderer;
            }
        };
            JScrollPane scrollPane = new JScrollPane(table);

           
            JFrame leaderboardFrame = new JFrame("Leaderboard");
            leaderboardFrame.setSize(830, 600); // Resize the frame
            leaderboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            leaderboardFrame.setLocationRelativeTo(null);

            BackgroundPanel panel = new BackgroundPanel(); // BackgroundPanel to match the main menu background
            panel.setLayout(null); // Disable layout manager

            // Set bounds for components
            scrollPane.setBounds(265, 100, 300, 380); // Table bounds
            panel.add(scrollPane);

            table.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

            JButton backButton = new JButton("Back");
            styleButton(backButton);
            backButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    leaderboardFrame.dispose();
                    previousFrame.setVisible(true);
                }
            });
            backButton.setBounds(340, 500, 150, 30); // Back button bounds
            panel.add(backButton);

            leaderboardFrame.add(panel);
            leaderboardFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Place");
        for (int column = 1; column <= columnCount; column++) {
            String columnName = metaData.getColumnName(column);
            if (columnName.equals("HighestScore")) {
                columnName = "Highest Score"; // Modify the column name
            }
            columnNames.add(columnName);
        }
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        int rowNum = 1;
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<Object>();
            vector.add(rowNum); // Add row number
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(resultSet.getObject(columnIndex));
            }
            data.add(vector);
            rowNum++;
        }
        return new DefaultTableModel(data, columnNames);
    }

    private static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.GRAY);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public static void updateLeaderboard() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            PreparedStatement playerStatement = connection.prepareStatement("SELECT DISTINCT Name FROM Player");
            ResultSet playerResultSet = playerStatement.executeQuery();

            while (playerResultSet.next()) {
                String playerName = playerResultSet.getString("Name");

                PreparedStatement leaderboardStatement = connection.prepareStatement("SELECT HighestScore FROM LeaderBoard WHERE Player = ?");
                leaderboardStatement.setString(1, playerName);
                ResultSet leaderboardResultSet = leaderboardStatement.executeQuery();

                if (leaderboardResultSet.next()) {
                    int currentScore = leaderboardResultSet.getInt("HighestScore");
                    int playerScore = getPlayerScore(playerName);
                    if (playerScore > currentScore) {
                        PreparedStatement updateStatement = connection.prepareStatement("UPDATE LeaderBoard SET HighestScore = ? WHERE Player = ?");
                        updateStatement.setInt(1, playerScore);
                        updateStatement.setString(2, playerName);
                        updateStatement.executeUpdate();
                        updateStatement.close();
                    }
                } else {
                    PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO LeaderBoard (Player, HighestScore) VALUES (?, ?)");
                    insertStatement.setString(1, playerName);
                    insertStatement.setInt(2, getPlayerScore(playerName));
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }

                leaderboardResultSet.close();
                leaderboardStatement.close();
            }

            playerResultSet.close();
            playerStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getPlayerScore(String playerName) {
        int playerScore = 0;
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            PreparedStatement playerScoreStatement = connection.prepareStatement("SELECT Score FROM Player WHERE Name = ?");
            playerScoreStatement.setString(1, playerName);
            ResultSet playerScoreResultSet = playerScoreStatement.executeQuery();

            if (playerScoreResultSet.next()) {
                playerScore = playerScoreResultSet.getInt("Score");
            }
            playerScoreResultSet.close();
            playerScoreStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerScore;
    }
    
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("resources/TankistFightBf.jpeg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
            }
        }
    }
}
