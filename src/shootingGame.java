import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class shootingGame {
    public static void main(String[] args) {
        new GameFrame();
    }
    public static class GameFrame extends JFrame {

        GameFrame() {
            this.add(new GamePanel());
            this.setTitle("2D Shooting Game");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }
    }
    public static class GamePanel extends JPanel implements ActionListener, KeyListener {
        int score = 0;
        int playerHealth = 5;
        boolean gameOver = false;
        ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();

        static final int WIDTH = 800;
        static final int HEIGHT = 500;

        Timer timer;

        Player player;
        ArrayList<Bullet> bullets = new ArrayList<>();
        ArrayList<Enemy> enemies = new ArrayList<>();

        GamePanel() {
            ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
            this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            this.setBackground(Color.black);
            this.setFocusable(true);
            this.addKeyListener(this);

            player = new Player(350, 400);

            spawnEnemy();

            timer = new Timer(17, this); // ~60 FPS
            timer.start();
        }

        public void spawnEnemy() {
            enemies.add(new Enemy((int)(Math.random()*750), 0));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (gameOver) {
                g.setColor(Color.red);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("GAME OVER", 250, 300);
                return;
            }

            player.draw(g);

            for (Bullet b : bullets)
                b.draw(g);

            for (Enemy e : enemies)
                e.draw(g);

            for (EnemyBullet eb : enemyBullets)
                eb.draw(g);

            g.setColor(Color.white);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Health: " + playerHealth, 10, 40);
        }

        public void actionPerformed(ActionEvent e) {

            if (gameOver) return;
            if(enemies.size() < 2) {
                if (Math.random() < 0.02) {
                    spawnEnemy();
                }
            }
            player.move();

            for (Bullet b : bullets)
                b.move();

            for (Enemy enemy : enemies) {
                enemy.move();

                // Random shooting
                if (Math.random() < 0.01) {
                    enemyBullets.add(new EnemyBullet(enemy.x + 20, enemy.y));
                }
            }

            for (EnemyBullet eb : enemyBullets)
                eb.move();
            bullets.removeIf(b -> b.y < 0);
            enemyBullets.removeIf(b -> b.y > HEIGHT);
            enemies.removeIf(enemy -> enemy.y > HEIGHT);
            checkCollision();
            repaint();
            enemies.removeIf(enemy -> enemy.y > HEIGHT);

        }

        public void checkCollision() {

            for (int i = 0; i < bullets.size(); i++) {
                for (int j = 0; j < enemies.size(); j++) {
                    if (bullets.get(i).getBounds().intersects(enemies.get(j).getBounds())) {
                        bullets.remove(i);
                        enemies.remove(j);
                        score += 10;

                        return;
                    }
                }
            }

            // Enemy bullet hitting player
            for (int i = 0; i < enemyBullets.size(); i++) {
                if (enemyBullets.get(i).getBounds().intersects(player.getBounds())) {
                    enemyBullets.remove(i);
                    playerHealth--;

                    if (playerHealth <= 0) {
                        gameOver = true;
                        timer.stop();
                    }
                    return;
                }
            }

        }

        // Key Controls
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                player.left = true;

            if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                player.right = true;

            if (e.getKeyCode() == KeyEvent.VK_SPACE)
                bullets.add(new Bullet(player.x + 20, player.y));
        }

        public void keyReleased(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_LEFT)
                player.left = false;

            if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                player.right = false;
        }

        public void keyTyped(KeyEvent e) {}
    }

    //player class
    public static class Player {

        int x, y;
        int speed = 10;
        boolean left , right;

        Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            if (left && x > 0)
                x -= speed;

            if (right && x < 750)
                x += speed;
        }

        public void draw(Graphics g) {
            g.setColor(Color.green);
            g.fillRect(x, y, 50, 20);
        }
        public Rectangle getBounds() {
            return new Rectangle(x, y, 50, 20);
        }
    }

    //bullet class
    public static class Bullet {

        int x, y;
        int speed = 9;

        Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            y -= speed;
        }

        public void draw(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(x, y, 7, 13);
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 2, 10);
        }
    }

    //enemy class
    public static class Enemy {

        int x, y;
        int speed = 2;

        Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            y += speed;
        }

        public void draw(Graphics g) {
            g.setColor(Color.red);
            g.fillRect(x, y, 40, 20);
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 40, 20);
        }
    }

    public static class EnemyBullet {

        int x, y;
        int speed = 4;

        EnemyBullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move() {
            y += speed;
        }

        public void draw(Graphics g) {
            g.setColor(Color.yellow);
            g.fillRect(x, y, 5, 10);
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 5, 10);
        }
    }
}