package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

import static byow.Core.Position.RANDOM;
import static byow.lab12.HexWorld.getRandomSize;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    public static ArrayList<Room> roomsList = new ArrayList<>();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] world = null;

        long SEED = 123;
        Random RANDOM = new Random(SEED);

        return world;
    }

    /** Fill the world with nothing. */
    private static void fillTheWorldWithNothing(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j ++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }



    /*public static void fillPoints(TETile[][] world) {
        long SEED = 123;
        Position position = new Position(SEED, WIDTH, HEIGHT);
        int [][] roomPoints = position.getRoomPoints();
        for (int i = 0; i < 15; i++) {
            int x = roomPoints[i][0];
            int y = roomPoints[i][1];
            System.out.println(x);
            if (x != 0) {
                world[x][y] = Tileset.SAND;
            }
        }
    }*/

    public static void fillRooms(TETile[][] world) {
        long SEED = 1237;
        // 得到房间的坐标点
        Position position = new Position(SEED);
        int [][] roomPoints = position.getRoomPoints();

        // 在每个点上创建房间
        for (int i = 0; i < roomPoints.length; i++) {
            int x = roomPoints[i][0];
            int y = roomPoints[i][1];

            // 跳过空点
            if (x == 0 && y == 0 && i > 0) {
                break;
            }

            // 随机房间大小
            int roomWidth = RANDOM.nextInt(10) + 2;   // 2-11
            int roomHeight = RANDOM.nextInt(10) + 2;  // 2-11

            Room newRoom = new Room(x, y, roomWidth, roomHeight);

            // 检查是否与现有房间重叠
            if (!isOverlapping(newRoom)) {
                newRoom.draw(world);
                roomsList.add(newRoom);
            }
        }
    }

    // 检查新房间是否与现有房间重叠
    private static boolean isOverlapping(Room newRoom) {
        for (Room existingRoom : roomsList) {
            if (roomsOverlap(newRoom, existingRoom)) {
                return true;
            }
        }
        return false;
    }

    // 判断两个房间是否重叠（包含边界缓冲）
    private static boolean roomsOverlap(Room r1, Room r2) {
        int buffer = 2;  // 房间之间至少2格距离

        // r1 的边界（加上缓冲区）
        int r1Left = r1.getX() - buffer;
        int r1Right = r1.getX() + r1.getWidth() + buffer;
        int r1Bottom = r1.getY() - buffer;
        int r1Top = r1.getY() + r1.getHeight() + buffer;

        // r2 的边界
        int r2Left = r2.getX();
        int r2Right = r2.getX() + r2.getWidth();
        int r2Bottom = r2.getY();
        int r2Top = r2.getY() + r2.getHeight();

        // 检查是否重叠
        return !(r1Right < r2Left ||
                r1Left > r2Right ||
                r1Top < r2Bottom ||
                r1Bottom > r2Top);
    }

    // 连接所有房间（链式连接）
    private static void connectAllRooms(TETile[][] world, ArrayList<Room> rooms) {
        for (int i = 0; i < roomsList.size() - 1; i++) {
            Hallway.connectRooms(world, rooms.get(i), rooms.get(i + 1));
        }
    }

    // 修复整个地图的墙壁
    private static void fixAllWalls(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                // 如果是空白且相邻有地板，就放墙壁
                if (world[x][y].equals(Tileset.NOTHING) && hasAdjacentFloor(world, x, y)) {
                    world[x][y] = Tileset.WALL;
                }
            }
        }
    }

    // 检查此空白区域是否为走廊拐角
    private static boolean hasAdjacentFloor(TETile[][] world, int x, int y) {
        // 检查上下左右四个方向
        int[][] directions = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
                if (world[nx][ny].equals(Tileset.FLOOR)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillTheWorldWithNothing(world);
        fillRooms(world);
        connectAllRooms(world, roomsList);
        fixAllWalls(world);

        ter.initialize(WIDTH, HEIGHT);

        ter.renderFrame(world);
    }
}
