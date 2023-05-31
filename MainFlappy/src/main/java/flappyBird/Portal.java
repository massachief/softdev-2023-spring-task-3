package flappyBird;

import java.awt.*;
import java.util.Random;

public class Portal {
    public double jumpHeight = 10;
    public double gravity = 2;
    private final double maxGravity = 4;
    private final double minGravity = -4;

    private static final Random rand = new Random();
    public static int height = 120;
    public static int width = 10;

    private static final int minX = 1000;
    private static final int maxX = 1920;
    private int spawnX;
    private int spawnY;
    public Rectangle shape;

    Portal() {
        this.spawnX = rand.nextInt((maxX - minX) + 1) + maxX;
        this.spawnY = rand.nextInt(200) + 500;
        this.shape = new Rectangle(spawnX, spawnY, width, height);
        this.generateNewGravity();
    }

    Portal(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
        this.shape = new Rectangle(spawnX, spawnY, width, height);
        this.generateNewGravity();
    }

    public void generateNewGravity() {


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        gravity = rand.nextDouble((maxGravity - minGravity) + 1) + minGravity;
        jumpHeight = rand.nextDouble(20) + 5;
    }
}
