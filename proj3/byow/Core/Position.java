package byow.Core;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Stack;
import static byow.Core.Engine.WIDTH;
import static byow.Core.Engine.HEIGHT;


import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Position {
    public static final int MAX_ROOMS = 30;
    public static int[][] roomPoints = new int[MAX_ROOMS][2];
    public static Random RANDOM;
    public static int minDistance = 10;
    public int currentX;
    public int currentY;

    public Position (long seed) {
        RANDOM = new Random(seed);
    }

    public static void fillRandomPoints() {
        int count = 0;
        for (int i = 0; i < MAX_ROOMS; i++) {
            if (randomForPositon()) {
                int x = RANDOM.nextInt(WIDTH - 11) + 5;
                int y = RANDOM.nextInt(HEIGHT - 11) + 5;

                // 检查是否与已有房间太近
                boolean tooClose = false;
                for (int j = 0; j < count; j++) {
                    int dx = x - roomPoints[j][0];
                    int dy = y - roomPoints[j][1];
                    if (Math.abs(dx) < minDistance && Math.abs(dy) < minDistance) {
                        tooClose = true;
                        break;
                    }
                }

                if (!tooClose) {
                    roomPoints[count][0] = x;
                    roomPoints[count][1] = y;
                    count++;
                }
            }
        }

    }

    public static int[][] getRoomPoints() {
        fillRandomPoints();
        return roomPoints;
    }

    public static int[][] sort(int[][] roomPoints) {
        // 最小堆将points按照从左上到右下的顺序排序
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]*a[1]));
        for (int i = 0; i < MAX_ROOMS; i++) {
            if (roomPoints[i][0] != 0) {
                pq.offer(new int[]{roomPoints[i][0], roomPoints[i][0]});
            }
        }

        // 讲points放回二维数组，排序顺序从左上到右下或相反
        int [] point;
        if (randomForSort()) {
            for (int i = 0; i < MAX_ROOMS; i++) {
                if (!pq.isEmpty()) {
                    point = pq.poll();
                    roomPoints[i][0] = point[0];
                    roomPoints[i][1] = point[1];
                } else {
                    break;
                }
            }
        } else {
            Stack<int[]> stack = new Stack<>();
            while (!pq.isEmpty()) {
                point = pq.poll();
                stack.push(point);
            }

            for (int i = 0; i < MAX_ROOMS; i++) {
                if (!stack.isEmpty()) {
                    point = stack.pop();
                    roomPoints[i][0] = point[0];
                    roomPoints[i][1] = point[1];
                } else {
                    break;
                }
            }
        }
        return roomPoints;
    }

    private static boolean randomForSort() {
        int number = RANDOM.nextInt(2);
        switch (number) {
            case 0: return true;
            case 1: return false;
        }
        return false;
    }

    private static boolean randomForPositon() {
        int number = RANDOM.nextInt(5);
        switch (number) {
            case 0: return true;
            case 1: return true;
            case 2: return true;
            case 3: return true;
            case 4: return false;
        }
        return false;
    }
}
