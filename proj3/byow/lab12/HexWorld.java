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
    private static final int numberOfHex = 19;

    private static final long SEED = 23454;
    private static final Random RANDOM = new Random(SEED);

    public static class KeyPoints {
        private int index;                                  // the index of every key point in array keyPoints
        private final int size;                             // the size of hexagonal region
        private int[][] keyPoints;                          // to save key points
        private static final int partOneNodeCount = 15;     // the number of points part one saved

        public KeyPoints(int size) {
            this.size = size;
            this.index = 0;
            this.keyPoints = new int[numberOfHex][2];
        }

        /**
         * To create and 2D array to save all X and Y of keyPoints
         *
         * @return the 2D array
         */
        private void buildKeyPoints() {          // 普通内部类中不能定义 static 成员
            // Update keyPoints with partOne and partTwo
            int[] lastPointOfPartOne = updatePartOne();

            updatePartTwo(lastPointOfPartOne[0], lastPointOfPartOne[1]);
        }

        public int[][] getKeyPoints() {
            buildKeyPoints();

            return keyPoints;
        }

        /**
         * Update part one key points, save 15 points.
         *
         * @return an array includes X and Y for the last middle point
         */
        private int[] updatePartOne() {
            // To save key points
            int x = (WIDTH - size) / 2;             // X of the current point
            int y = 0;                              // Y of the current point
            int middlePointX = x;                   // X of the current middle point
            int middlePointY = y;                   // Y of the current middle point
            int[] lastPoint = new int[2];           // array includes X and Y for the last middle point

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
                middlePointY = middlePointY + 2*size;

                // Update current point
                //x = middlePointX;
                //y = middlePointY;
            }

            // save the X and Y for last middle point
            lastPoint[0] = middlePointX;
            lastPoint[1] = middlePointY - 2*size;

            return lastPoint;
        }

        /**
         * Update part two key points, save 4 points.
         *
         * @param x the X of the last middle point after part one
         * @param y the Y of the last middle point after part one
         */
        private void updatePartTwo(int x, int y) {
            int index = partOneNodeCount;
            int currentX = x;
            int currentY = y;

            // Save two middle points
            for (int i = 0; i < 2; i++) {
                currentY = currentY + 2*size;
                keyPoints[index][0] = currentX;
                keyPoints[index][1] = currentY;
                index = index + 1;
            }

            // Save the right point
            currentX = x + (2*size - 1);
            currentY = y + 3*size;
            keyPoints[index][0] = currentX;
            keyPoints[index][1] = currentY;
            index = index + 1;

            // Save the left point
            currentX = x - (2*size - 1);
            keyPoints[index][0] = currentX;
            keyPoints[index][1] = currentY;
        }
    }

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

    private static int[][] saveKeyPoints(int size) {
        KeyPoints keyPoints = new KeyPoints(size);

        return keyPoints.getKeyPoints();
    }

    public static int[][] saveHexPoints(int x, int y, int size) {
        int index = 0;                        // index for point in the array
        int number = (4*size - 2) * size;            // the number of points(两个梯形的面积公式)
        int[][] points = new int[number][2];  // 2D array to save points
        int currentX = x;
        int currentY = y;

        // 遍历中间的长方形
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 2*size; j++) {
                points[index][0] = currentX;
                points[index][1] = currentY;

                currentY = currentY + 1;
                index = index + 1;
            }
            currentX = currentX + 1;
            currentY = y;
        }

        // Update current X and Y
        currentX = x;
        currentY = y;

        int j = 2 * size;   // 竖方向的遍历因子

        // 遍历左边的梯形
        for (int i = 0; i < size-1; i++) {
            // 更新遍历因子
            j = j - 2;

            // Update X and Y to current point
            currentX = currentX - 1;
            currentY = currentY + 1;

            for (int m = 0; m < j; m++) {
                points[index][0] = currentX;
                points[index][1] = currentY;

                currentY = currentY + 1;
                index = index + 1;
            }
            currentY = currentY - j;
        }

        // Update current X, Y and 遍历因子
        currentX = x + size - 1;
        currentY = y;
        j = 2 * size;

        // 遍历右边的梯形
        for (int i = 0; i < size-1; i++) {
            // 更新遍历因子
            j = j - 2;

            // Update X and Y to current point
            currentX = currentX + 1;
            currentY = currentY + 1;

            for (int m = 0; m < j; m++) {
                points[index][0] = currentX;
                points[index][1] = currentY;

                currentY = currentY + 1;
                index = index + 1;
            }

            currentY = currentY - j;
        }

        return points;
    }

    /**
     * To create a suitable black world with the size of hexagon.
     *
     * @param size the size of hexagon
     * @return a black world with WIDTH and HEIGHT
     */
    private static TETile[][] buildBlackWorld(int size) {
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

    /**
     * Fills the given 2D array of tiles with RANDOM tiles.
     * @param size the size of hexagon
     */
    public static TETile[][] fillWithTiles(int size) {
        int x;
        int y;
        int[][] hexPoints;
        TETile currentHex;

        // Get black world with given size
        TETile[][] blackWorld = buildBlackWorld(size);

        // Get key points with given size
        int[][] keyPoints = saveKeyPoints(size);

        // Make up the black world
        for (int i = 0; i < keyPoints.length; i++) {
            x = keyPoints[i][0];
            y = keyPoints[i][1];
            System.out.println("(" + x + "," + y + ")");

            currentHex = randomTile();
            hexPoints = saveHexPoints(x, y, size);

            for (int j = 0; j < hexPoints.length; j++) {
                x = hexPoints[j][0];
                y = hexPoints[j][1];

                blackWorld[x][y] = currentHex;
            }
        }

        return blackWorld;
    }

    /** Picks a RANDOM tile with a 20% change of being
     *  grass, 20% chance of being a flower, 20%
     *  chance of being a sand, 20% chance of being a
     *  tree, and 20% chance of being a mountain.
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

        int size = getRandomSize();
        TETile[][] randomTiles = fillWithTiles(size);

        ter.initialize(WIDTH, HEIGHT);

        ter.renderFrame(randomTiles);
    }


}