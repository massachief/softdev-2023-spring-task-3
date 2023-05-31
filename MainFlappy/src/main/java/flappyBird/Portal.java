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
    private int spawnX;
    private int spawnY;
    public Rectangle shape;
    Portal(int x, int y) {
        this.spawnX = x;
        this.spawnY = y;
        this.shape = new Rectangle(spawnX, spawnY, width, height);
        this.generateNewGravity();
    }

    public void generateNewGravity() {
        gravity = rand.nextDouble((maxGravity - minGravity) + 1) + minGravity;
        jumpHeight = rand.nextDouble(20) + 5;
    }
}
