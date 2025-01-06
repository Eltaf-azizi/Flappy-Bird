import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImage;

    //Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;
    private final Timer placePipesTimer;
    private int openingSpace;
    private int score;

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
    }
    
    
    @Override
    public void keyTyped(KeyEvent e) {}


    @Override
    public void keyReleased(KeyEvent e) {}


    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64; // scaled by 1/6
    int pipeHeight = 512;


    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }


    // Game logic
    Bird bird;
    int velocityX = -4;  // Move pipes to the left speed (simulates bird moving right)
    int velocityY = -9;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();


    Timer gameloop;
    boolean gameOver = false;


    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        // Load images
        backgroundImg = new ImageIcon("flappybirdng.png").getImage();
        birdImg = new ImageIcon("flappybird.png").getImage();
        topPipeImg = new ImageIcon("toppipe.png").getImage();
        bottomPipeImage = new ImageIcon("bottompipeimage.png").getImage();

        // Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList();


        // place pipes timer
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        placePipesTimer.start();

        // Game timer
        gameloop = new Timer(1000 / 60, this); // 1000/60 = 16.6
        gameloop.start();
    }


    public void placePipes() {
        // (0-1) * pipeHeight/2 -> (0-256)

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));

         Pipe topPipe = new Pipe(topPipeImg);
         topPipe.y = randomPipeY;
         pipes.add(topPipe);


         Pipe bottomPipe = new Pipe(bottomPipeImage);
         bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
         pipes.add(bottomPipe);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {

        // background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i=0; i< pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);


        // pipes
        for(int i=0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;


            if (!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; // 0.5 because there are 2 pipes! so 0.5*2 = 1 for each set of pipes
            }

            if (collision(bird, pipe)){
                gameOver = true;
            }
        }


        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }
    

    public boolean collision(Bird a, Pipe b) {
        return  a.x < b.x + b.width && // a's top left corner doesn't reach b's right corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && //a's top left corner doesn't reach b's bottom left corner
                a.y + b.height > b.y;  // a's bottom left corner passes b's top left corner
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird flappyBird = new FlappyBird();

        frame.add(flappyBird);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        flappyBird.gameloop.start();
    }
}
