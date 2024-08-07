import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Flybirds extends JPanel implements ActionListener, KeyListener, MouseListener {
    int bwidth = 300;
    int bheight = 600;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image restartImg;

    // Bird
    int birdX = bwidth / 8;
    int birdY = bheight / 2;
    int birdwidth = 34;
    int birdheight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = bwidth;
    int pipeY = 0;
    int pipewidth = 64; // scaled by 1/6
    int pipeheight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int height = pipeheight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game log
    Bird bird;
    int velocityX = -4; // Move pipes to the left speed (simulates the bird moving right)
    int velocityY = 0; // Move bird up and down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    int delay = 1000 / 60;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;

    // Restart button properties
    int restartX = bwidth / 2 - 50; // Button position (centered horizontally)
    int restartY = bheight / 2 - 50; // Button position (centered vertically)
    int restartWidth = 100; // Button width
    int restartHeight = 50; // Button height

    public Flybirds() {
        setPreferredSize(new Dimension(bwidth, bheight));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        // Load images
        backgroundImg = loadImage("./flybg.png");
        birdImg = loadImage("./flybird.png");
        topPipeImg = loadImage("./toppipe.png");
        bottomPipeImg = loadImage("./bottompipe.png");
        restartImg = loadImage("./restart.png");

        // Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // Pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int space = 100;
                int pipeY = random.nextInt(bheight / 2);
                pipes.add(new Pipe(topPipeImg));
                pipes.add(new Pipe(bottomPipeImg));
                pipes.get(pipes.size() - 2).x = bwidth;
                pipes.get(pipes.size() - 2).y = pipeY - pipeheight;
                pipes.get(pipes.size() - 1).x = bwidth;
                pipes.get(pipes.size() - 1).y = pipeY + space;
            }
        });

        gameLoop = new Timer(delay, this);
        gameLoop.start();
        placePipesTimer.start();
    }

    private Image loadImage(String path) {
        Image img = null;
        try {
            img = new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            System.out.println("Error loading image: " + path);
            e.printStackTrace();
        }
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, bwidth, bheight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw the restart button when the game is over
        if (gameOver) {
            g.drawImage(restartImg, restartX, restartY, restartWidth, restartHeight, null);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 32));
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 2 pipes, 0.5 * 2 = 1
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        if (bird.y > bheight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x && // a's top right corner passes b's top left corner
               a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom right corner
               a.y + a.height > b.y;   // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                restartGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Check if the mouse click is within the restart button's bounds
        if (gameOver && mouseX >= restartX && mouseY >= restartY &&
            mouseX <= restartX + restartWidth && mouseY <= restartY + restartHeight) {
            restartGame();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    // Method to restart the game
    private void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placePipesTimer.start();
    }

    // Main method to start the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        Flybirds game = new Flybirds();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
