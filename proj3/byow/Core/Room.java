package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import static byow.Core.Engine.WIDTH;
import static byow.Core.Engine.HEIGHT;

public class Room {
    private int x;      // 房间中心的 x 坐标
    private int y;      // 房间中心的 y 坐标
    private int width;  // 房间宽度
    private int height; // 房间高度

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // 在地图上绘制这个房间
    public void draw(TETile[][] world) {
        // 绘制地板
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (i >= 0 && i < world.length && j >= 0 && j < world[0].length) {
                    if (i == WIDTH - 1 || j == HEIGHT - 1) {
                        world[i][j] = Tileset.WALL;
                    } else {
                        world[i][j] = Tileset.FLOOR;
                    }
                }
            }
        }

        // 绘制墙壁（围绕地板）
        drawWalls(world);
    }

    private void drawWalls(TETile[][] world) {
        // 上下墙
        for (int i = x - 1; i <= x + width; i++) {
            setWall(world, i, y - 1);      // 下墙
            setWall(world, i, y + height); // 上墙
        }

        // 左右墙
        for (int j = y - 1; j <= y + height; j++) {
            setWall(world, x - 1, j);      // 左墙
            setWall(world, x + width, j);  // 右墙
        }
    }

    private void setWall(TETile[][] world, int i, int j) {
        if (i >= 0 && i < world.length && j >= 0 && j < world[0].length) {
            // 只在空白处放置墙壁，不覆盖地板
            if (world[i][j] == Tileset.NOTHING) {
                world[i][j] = Tileset.WALL;
            }
        }
    }

    // Getter 方法（后面连接走廊时会用到）
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // 获取房间中心点（用于连接走廊）
    public int getCenterX() { return x + width / 2; }
    public int getCenterY() { return y + height / 2; }
}
