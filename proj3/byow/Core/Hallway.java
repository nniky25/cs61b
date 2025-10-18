package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

import static byow.Core.Position.RANDOM;

public class Hallway {

    // 连接两个房间
    public static void connectRooms(TETile[][] world, Room room1, Room room2) {
        int x1 = room1.getCenterX();
        int y1 = room1.getCenterY();
        int x2 = room2.getCenterX();
        int y2 = room2.getCenterY();

        // 随机选择先横向还是先纵向
        if (randomForSort()) {
            // 先横向，再纵向
            drawHorizontalHallway(world, x1, x2, y1);
            drawVerticalHallway(world, y1, y2, x2);
        } else {
            // 先纵向，再横向
            drawVerticalHallway(world, y1, y2, x1);
            drawHorizontalHallway(world, x1, x2, y2);
        }
    }

    // 绘制横向走廊
    private static void drawHorizontalHallway(TETile[][] world, int x1, int x2, int y) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);

        for (int x = minX; x <= maxX; x++) {
            if (isInBounds(world, x, y)) {
                world[x][y] = Tileset.FLOOR;
                // 添加墙壁
                if (isInBounds(world, x, y + 1) && world[x][y + 1] == Tileset.NOTHING) {
                    world[x][y + 1] = Tileset.WALL;
                }
                if (isInBounds(world, x, y - 1) && world[x][y - 1] == Tileset.NOTHING) {
                    world[x][y - 1] = Tileset.WALL;
                }
            }
        }
    }

    // 绘制纵向走廊
    private static void drawVerticalHallway(TETile[][] world, int y1, int y2, int x) {
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);

        for (int y = minY; y <= maxY; y++) {
            if (isInBounds(world, x, y)) {
                world[x][y] = Tileset.FLOOR;
                // 添加墙壁
                if (isInBounds(world, x + 1, y) && world[x + 1][y] == Tileset.NOTHING) {
                    world[x + 1][y] = Tileset.WALL;
                }
                if (isInBounds(world, x - 1, y) && world[x - 1][y] == Tileset.NOTHING) {
                    world[x - 1][y] = Tileset.WALL;
                }
            }
        }
    }

    private static boolean randomForSort() {
        int number = RANDOM.nextInt(2);
        switch (number) {
            case 0: return true;
            case 1: return false;
        }
        return false;
    }

    private static boolean isInBounds(TETile[][] world, int x, int y) {
        return x >= 0 && x < world.length && y >= 0 && y < world[0].length;
    }
}
