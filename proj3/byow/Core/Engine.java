package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static byow.Core.Position.RANDOM;
import static byow.lab12.HexWorld.getRandomSize;

public class Engine {
    static TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static boolean gameOver;
    private static long SEED = 222;
    public static final int HEIGHT = 40;
    public static int[] role = new int[2];
    public static int[] door = new int[2];
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
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
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
        return world;
    }

    /**
     * Fill the world with nothing.
     */
    private static void fillTheWorldWithNothing(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
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
        // 得到房间的坐标点
        Position position = new Position(SEED++);
        int[][] roomPoints = position.getRoomPoints();

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
        gameOver = false;
        TETile[][] world = new TETile[WIDTH][HEIGHT];

        world = madeNewWorld(world);

        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);

        while (!gameOver) {
            updateWorld(world);
        }
    }

    public static TETile[][] madeNewWorld(TETile[][] world) {
        roomsList.clear();
        fillTheWorldWithNothing(world);
        fillRooms(world);
        connectAllRooms(world, roomsList);
        fixAllWalls(world);
        setRole(world);
        setDoor(world);

        return world;
    }

    public static void updateRoleInWorld(TETile[][] world) {
        world[role[0]][role[1]] = Tileset.AVATAR;
    }

    public static void setRole(TETile[][] world) {
        Room roleRoom = roomsList.get(0);
        int x = roleRoom.getX();
        int y = roleRoom.getY();
        role[0] = x;
        role[1] = y;

        world[role[0]][role[1]] = Tileset.AVATAR;
    }

    public static void setDoor(TETile[][] world) {
        Room doorRoom = roomsList.getLast();
        int x = doorRoom.getX();
        int y = doorRoom.getY();
        door[0] = x;
        door[1] = y;
        world[door[0]][door[1]] = Tileset.FLOWER;
    }

    public static boolean run(char step, TETile[][] world) {
        boolean canrun = false;
        int[] station = new int[2];
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        if (canMove()) {
            if (Character.toLowerCase(step) == 'a') {
                station[0] = role[0] + directions[0][0];
                station[1] = role[1];
                if (world[station[0]][station[1]].equals(Tileset.FLOOR)) {
                    world[role[0]][role[1]] = Tileset.FLOOR;
                    role[0] = station[0];
                    role[1] = station[1];
                    canrun = true;
                } else if (world[station[0]][station[1]].equals(Tileset.FLOWER)) {
                    madeNewWorld(world);
                    canrun = true;
                }
            } else if (Character.toLowerCase(step) == 'd') {
                station[0] = role[0] + directions[1][0];
                station[1] = role[1];
                if (world[station[0]][station[1]].equals(Tileset.FLOOR)) {
                    world[role[0]][role[1]] = Tileset.FLOOR;
                    role[0] = station[0];
                    role[1] = station[1];
                    canrun = true;
                } else if (world[station[0]][station[1]].equals(Tileset.FLOWER)) {
                    madeNewWorld(world);
                    canrun = true;
                }
            } else if (Character.toLowerCase(step) == 's') {
                station[1] = role[1] + directions[3][1];
                station[0] = role[0];
                if (world[station[0]][station[1]].equals(Tileset.FLOOR)) {
                    world[role[0]][role[1]] = Tileset.FLOOR;
                    role[0] = station[0];
                    role[1] = station[1];
                    canrun = true;
                } else if (world[station[0]][station[1]].equals(Tileset.FLOWER)) {
                    madeNewWorld(world);
                    canrun = true;
                }
            } else if (Character.toLowerCase(step) == 'w') {
                station[1] = role[1] + directions[2][1];
                station[0] = role[0];
                    if (world[station[0]][station[1]].equals(Tileset.FLOOR)) {
                        world[role[0]][role[1]] = Tileset.FLOOR;
                        role[0] = station[0];
                        role[1] = station[1];
                        canrun = true;
                    } else if (world[station[0]][station[1]].equals(Tileset.FLOWER)) {
                        madeNewWorld(world);
                        canrun = true;
                    }
            }
        }
        return canrun;
    }

    public static boolean canMove() {
        if (role[0] > 0 && role[0] < WIDTH && role[1] > 0 && role[1] < HEIGHT) {
            return true;
        }
        return false;
    }

    /*public void startGame1() {
        //TODO: Set any relevant variables before the game starts
        //TODO: Establish Engine loop
        gameOver = false;

        while (!gameOver) {
            updateWorld(world);
        }
    }*/

    public static void updateWorld(TETile[][] world) {
        //TODO: Read n letters of player input
        String input = "";  // 用于存储用户输入的字符串
        if (StdDraw.hasNextKeyTyped()) {
            // 获取按下的键
            char key = StdDraw.nextKeyTyped();

            // 检查字符
            run(key, world);
            updateRoleInWorld(world);
            input += key;  // 添加新字符

            // 清空屏幕
            StdDraw.clear(StdDraw.BLACK);

            // 更新显示地图
            ter.renderFrame(world);
        }
    }
}
