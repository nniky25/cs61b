package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static int WIDTH;
    private static int HEIGHT;
    private final static int partOneNodeCount = 15;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Get a random size automatically
     *
     * @return the random size
     */
    public static int getRandomSize() {
        int size = RANDOM.nextInt(4);
        size = size + 2;

        return size;
    }

    /**
     * To create a suitable black world with the size of hexagon.
     *
     * @param size the size of hexagon
     * @return a black world with WIDTH and HEIGHT
     */
    public static TETile[][] buildBlackWorld(int size) {
        // Calculate WIDTH and HEIGHT with size
        WIDTH = size * 11 - 6;
        HEIGHT = size * 10;

        // Build a black world with WIDTH and HEIGHT
        TETile[][] blackWorld = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                blackWorld[x][y] = Tileset.NOTHING;
            }
        }

        return blackWorld;
    }

    private static int[][] buildKeyPoints(int size) {
        int index = 0;                                  // current points index
        int[][] keyPoints = new int[WIDTH][HEIGHT];     // to save key points

        // update keyPoints to partOne
        keyPoints = updatePartOne(keyPoints, size);
        // update keyPoints to partTwo
        keyPoints = updatePartTwo(keyPoints, size, (WIDTH - size) / 2, 4*size);

        return keyPoints;
    }

    /**
     * Update part one key points, save 15 points.
     *
     * @param keyPoints the int[][] to save key points
     * @param size the size of hexagon
     * @return keyPoints
     */
    private static int[][] updatePartOne(int[][] keyPoints, int size) {
        // To save key points
        int index = 0;                          // current points index
        int x = (WIDTH - size) / 2;             // X of the current point
        int y = 0;                              // Y of the current point
        int middlePointX = x;                   // X of the current middle point
        int middlePointY = y;                   // Y of the current middle point

        for (int i = 0; i < 3; i++) {
            // Save the middle point
            keyPoints[index][0] = middlePointX;
            keyPoints[index][1] = middlePointY;
            index = index + 1;

            // Loop and save left points of the current middle point
            for (int j = 0; j < 2; j++) {
                // Move point to current point
                x = x - (2*size - 1);
                y = y + size;

                // Save the current point
                keyPoints[index][0] = x;
                keyPoints[index][1] = y;
                index = index + 1;
            }

            // Update the current point
            x = middlePointX;
            y = middlePointY;

            // Loop and save right points of the current middle point
            for (int j = 0; j < 2; j++) {
                // Move point to current point
                x = x + (2*size - 1);
                y = y + size;

                // Save the current point
                keyPoints[index][0] = x;
                keyPoints[index][1] = y;
                index = index + 1;
            }

            middlePointY = middlePointY - 2*size;
        }

        return keyPoints;
    }

    private static int[][] updatePartTwo(int[][] keyPoints, int size, int x, int y) {
        int index = partOneNodeCount;
        int currentX = x;
        int currentY = y;

        // Save middle points
        for (int i = 0; i < 2; i++) {
            currentY = currentY + 2*size;
            keyPoints[index][0] = currentX;
            keyPoints[index][1] = currentY;
            index = index + 1;
        }

        // Save right point
        currentX = x + (2*size - 1);
        currentY = y + size;
        keyPoints[index][0] = currentX;
        keyPoints[index][1] = currentY;
        index = index + 1;

        // Save left point
        currentX = x - (2*size - 1);
        keyPoints[index][0] = currentX;
        keyPoints[index][1] = currentY;

        return keyPoints;
    }


    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.GRASS;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.TREE;
            case 4: return Tileset.MOUNTAIN;
            default: return Tileset.MOUNTAIN;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        fillWithRandomTiles(randomTiles);

        ter.renderFrame(randomTiles);
    }


}
