import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javax.swing.*;

public class GradeCollectorGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class PlayerScore {
        String name;
        int score;

        PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    private int boardWidth;
    private int boardHeight;
    private int tileSize = 25;

    // Player
    private Tile player;
    private int currentScore;
    private Random random;

    // Grades
    private Tile grade;
    private double currentGradeValue;

    // Obstacles
    private ArrayList<Tile> obstacles;
    private ArrayList<Double> obstacleGrades;
    private boolean obstaclesVisible;
    private Timer obstacleTimer;
    private int obstacleAppearanceDuration = 2000; // 2 seconds

    // Game logic
    private int velocityX;
    private int velocityY;
    private Timer gameLoop;

    private boolean gameOver = false;
    private boolean gameStarted = false; // Flag to track if the game is started

    // Player
    private String playerName;

    // Leaderboard
    private ArrayList<PlayerScore> leaderboard;

    // Buttons
    private JButton playButton;
    private JButton retryButton;
    private JButton leaderboardButton;

    public GradeCollectorGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.white); // White background
        addKeyListener(this);
        setFocusable(true); // Make sure the panel is focusable

        player = new Tile(5, 5);

        random = new Random();
        placeGrade();

        obstacles = new ArrayList<>();
        obstacleGrades = new ArrayList<>();
        obstaclesVisible = false;

        velocityX = 1;
        velocityY = 0;

        // Game timer
        gameLoop = new Timer(100, this);

        // Obstacle timer
        obstacleTimer = new Timer(obstacleAppearanceDuration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obstaclesVisible = !obstaclesVisible;
                if (obstaclesVisible) {
                    placeObstacles();
                } else {
                    clearObstacles();
                }
                repaint();
            }
        });

        // Leaderboard
        leaderboard = new ArrayList<>();

        // Create buttons
        playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameStarted) {
                    startGame();
                } else {
                    stopGame();
                }
            }
        });

        retryButton = new JButton("Retry");
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLeaderboard();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        buttonPanel.add(retryButton);
        buttonPanel.add(leaderboardButton);

        JFrame frame = new JFrame("STR3SS - A Trailblazer Game");
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Request focus for the game panel
        requestFocusInWindow();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw Grid Lines
        g.setColor(Color.darkGray);
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Draw Grade
        drawTile(g, grade, new Color(0, 100, 0), String.valueOf(currentGradeValue)); // Dark green for food

        // Draw Obstacles
        if (obstaclesVisible) {
            for (int i = 0; i < obstacles.size(); i++) {
                Tile obstacle = obstacles.get(i);
                double obstacleGrade = obstacleGrades.get(i);
                drawTile(g, obstacle, Color.red, String.valueOf(obstacleGrade));
            }
        }

        // Draw Player
        drawPlayerTile(g, player, Color.blue);

        // Display Score
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String scoreMsg = "Score: " + currentScore;
        g.drawString(scoreMsg, boardWidth - g.getFontMetrics().stringWidth(scoreMsg) - 10, 30);

        // Game Over Message
        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String gameOverMsg = "Game Over!";
            g.drawString(gameOverMsg, (boardWidth - g.getFontMetrics().stringWidth(gameOverMsg)) / 2, boardHeight / 2);
        }

        // Welcome Message
        if (!gameStarted) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String welcomeMsg = "Ready get STR3SS!";
            g.drawString(welcomeMsg, (boardWidth - g.getFontMetrics().stringWidth(welcomeMsg)) / 2, boardHeight / 2 - 100);
        }
    }

    private void drawTile(Graphics g, Tile tile, Color color, String label) {
        g.setColor(color);
        g.fillRect(tile.x * tileSize, tile.y * tileSize, tileSize, tileSize);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(label, tile.x * tileSize + 5, tile.y * tileSize + 15);
    }

    private void drawPlayerTile(Graphics g, Tile tile, Color color) {
        g.setColor(color);
        g.fillRect(tile.x * tileSize, tile.y * tileSize, tileSize, tileSize);
    }

    public void placeGrade() {
        double[] positiveGrades = {1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5, 2.75, 3.0};
        currentGradeValue = positiveGrades[random.nextInt(positiveGrades.length)];
        grade = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));
    }

    public void placeObstacles() {
        double[] negativeGrades = {3.25, 3.5, 3.75, 4.0, 4.25, 4.5, 4.75, 5.0};
        obstacles.clear();
        obstacleGrades.clear();
        for (int i = 0; i < 10; i++) {
            Tile obstacle = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));
            obstacles.add(obstacle);
            obstacleGrades.add(negativeGrades[random.nextInt(negativeGrades.length)]);
        }
    }

    public void clearObstacles() {
        obstacles.clear();
        obstacleGrades.clear();
    }

    public void move() {
        // Collect positive grade
        if (collision(player, grade)) {
            currentScore += 1;
            placeGrade();
        }

        // Move player
        player.x += velocityX;
        player.y += velocityY;

        // Check game over conditions
        if (player.x < 0 || player.x >= boardWidth / tileSize ||
                player.y < 0 || player.y >= boardHeight / tileSize) {
            gameOver = true;
        }

        if (obstaclesVisible) {
            for (Tile obstacle : obstacles) {
                if (collision(player, obstacle)) {
                    gameOver = true;
                    break;
                }
            }
        }

        // Add obstacles based on score milestones
        if (currentScore == 15 || currentScore == 25 || currentScore == 35 || currentScore == 45 ||
            currentScore == 55 || currentScore == 65 || currentScore == 75 || currentScore == 85 ||
            currentScore == 95 || currentScore == 100) {
            addObstacles(3);
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
            if (gameOver) {
                stopGame();
                updateLeaderboard();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && velocityX == 0) {
            velocityX = -1;
            velocityY = 0;
        } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && velocityX == 0) {
            velocityX = 1;
            velocityY = 0;
        } else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && velocityY == 0) {
            velocityX = 0;
            velocityY = -1;
        } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && velocityY == 0) {
            velocityX = 0;
            velocityY = 1;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void startGame() {
        playerName = JOptionPane.showInputDialog(this, "Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }
        gameStarted = true;
        gameLoop.start(); // Start game timer
        obstacleTimer.start(); // Start obstacle appearance timer
        requestFocusInWindow();
    }

    public void stopGame() {
        gameStarted = false;
        gameLoop.stop(); // Stop game timer
        obstacleTimer.stop(); // Stop obstacle appearance timer
        obstaclesVisible = false;
        repaint();
        updateLeaderboard();
    }

    public void resetGame() {
        player = new Tile(5, 5);
        currentScore = 0;
        placeGrade();
        clearObstacles();
        velocityX = 1;
        velocityY = 0;
        gameOver = false;
        gameStarted = false; // Reset game started flag
        repaint();
    }

    private void updateLeaderboard() {
        // Create a new PlayerScore object with the current player's name and score
        PlayerScore newScore = new PlayerScore(playerName, currentScore);

        // Check if this score is already in the leaderboard
        boolean found = false;
        for (PlayerScore ps : leaderboard) {
            if (ps.name.equals(playerName)) {
                found = true;
                if (ps.score < currentScore) {
                    ps.score = currentScore; // Update the score if it's higher
                }
                break;
            }
        }

        // If not found, add the new score to the leaderboard
        if (!found) {
            leaderboard.add(newScore);
        }

        // Sort leaderboard by score (descending)
        Collections.sort(leaderboard, new Comparator<PlayerScore>() {
            @Override
            public int compare(PlayerScore ps1, PlayerScore ps2) {
                return Integer.compare(ps2.score, ps1.score);
            }
        });

        // Keep only top 5 scores
        if (leaderboard.size() > 5) {
            leaderboard = new ArrayList<>(leaderboard.subList(0, 5));
        }
    }

    private void showLeaderboard() {
        StringBuilder leaderboardText = new StringBuilder();
        leaderboardText.append("Leaderboard:\n");
        for (PlayerScore playerScore : leaderboard) {
            leaderboardText.append(playerScore.name).append(": ").append(playerScore.score).append("\n");
        }
        JOptionPane.showMessageDialog(this, leaderboardText.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to add specified number of obstacles to the game
    private void addObstacles(int numObstacles) {
        double[] negativeGrades = {3.25, 3.5, 3.75, 4.0, 4.25, 4.5, 4.75, 5.0};
        for (int i = 0; i < numObstacles; i++) {
            Tile obstacle = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));
            obstacles.add(obstacle);
            obstacleGrades.add(negativeGrades[random.nextInt(negativeGrades.length)]);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GradeCollectorGame(600, 600);
        });
    }
}
