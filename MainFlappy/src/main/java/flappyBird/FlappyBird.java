package flappyBird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.*;
import javax.sound.sampled.*;

public class FlappyBird implements ActionListener, MouseListener, KeyListener {
    public int lives;
    public double gravity = 2;
    public double jumpHeight = 10;
    public static FlappyBird flappyBird;
    public final int WIDTH = 1920, HEIGHT = 1080;
    public Renderer renderer;
    public Rectangle bird;
    public ArrayList<Rectangle> columns;
    public ArrayList<Portal> portals = new ArrayList<Portal>();
    public int ticks, yMotion, score;
    public boolean gameOver, started;

    public int nukeBombs = 0;
    public Random rand;

    public FlappyBird() {
        JFrame jframe = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        rand = new Random();

        jframe.add(renderer);
        jframe.setTitle("Flappy Bird");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        columns = new ArrayList<Rectangle>();


        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }

    public void playSound(String filename) {
        try {
            File yourFile = new File(filename);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        } catch (Exception e) {
            // Ignore
        }

    }

    public void addColumn(boolean start) {
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);
        if (rand.nextInt(0, 2) == 0) {
            portals.add(new Portal(WIDTH + width + columns.size() * 300, HEIGHT - height - 140 - Portal.height));
        }
        if (start) {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    public void nukeAllColumns() {
        columns = new ArrayList<>();
    }

    public void paintColumn(Graphics g, Rectangle column) {
        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void paintPortal(Graphics g, Portal portal) {
        if (portal.gravity >= 0) {
            g.setColor(Color.blue.darker());
        } else {
            g.setColor(Color.yellow);
        }
        g.fillRect(portal.shape.x, portal.shape.y, Portal.width, Portal.height);
    }

    public void jump() {
        playSound("jump.wav");

        if (gameOver) {
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            columns.clear();
            yMotion = 0;
            score = 0;
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
            gameOver = false;
        }

        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {
                yMotion = 0;
            }
            yMotion -= jumpHeight;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (columns.size() < 4) {
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);
        }
        int speed = 10;
        ticks++;
        if (started) {
            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                column.x -= speed;
            }
            for (Portal portal : portals) {
                portal.shape.x -= speed;
            }
            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += gravity;
            }
            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);
                if (column.x + column.width < 0) {
                    columns.remove(column);
                    if (column.y == 0) {
                        addColumn(false);
                    }
                }
            }
            bird.y += yMotion;
            for (Rectangle column : columns) {
                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
                    score++;
                }
                if (column.intersects(bird)) {
                    if (lives > 0) {
                        lives -= 1;
                        column.setSize(0, 0);
                    } else {
                        gameOver = true;
                    }
                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;
                    } else {
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }
            for (int i = 0; i < portals.size(); i++) {
                Portal portal = portals.get(i);
                if (portal.shape.intersects(bird)) {
                    if (rand.nextInt(4) == 0) {
                        nukeBombs += 1;
                    }
                    gravity = portal.gravity;
                    lives += 1;
                    jumpHeight = portal.jumpHeight;
                    if (gravity < 0) {
                        jumpHeight *= -1;
                    }
                    portal.shape.setSize(0, 0);
                }
                if (portal.shape.x + Portal.width < 0) {
                    portals.remove(i);
                }
            }
            if (bird.y > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
            }
            if (bird.y + yMotion >= HEIGHT - 120) {
                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;
            }
        }
        renderer.repaint();
    }

    public void repaint(Graphics g) {

        if (gravity > 0) {
            g.setColor(Color.cyan);
        } else {
            g.setColor(Color.PINK);
        }
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);
        for (Rectangle column : columns) {
            paintColumn(g, column);
        }

        for (Portal portal : portals) {
            paintPortal(g, portal);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));

        if (!started) {
            g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
        }
        g.drawString("Lives: " + lives + "   Boom " + nukeBombs, 75, 75);
        if (gameOver) {
            g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
            gravity = 2;
            jumpHeight = 10;
            portals = new ArrayList<>();
            nukeBombs = 0;
        }

        if (!gameOver && started) {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 150);
        }
    }

    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        jump();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            jump();
        }
        if (e.getKeyCode() == KeyEvent.VK_C) {
            if (nukeBombs > 0) {
                nukeAllColumns();
                playSound("nuke.wav");
                nukeBombs -= 1;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_G) {
            gravity *= -1;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
}
