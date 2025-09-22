package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.awt.Color;

/**
 * 达芬奇的数字蒙娜丽莎
 * 使用TETile系统重现永恒的微笑
 */
public class lisa {

    private static final int WIDTH = 40;
    private static final int HEIGHT = 50;

    // 创建蒙娜丽莎专用的"颜料"
    public static final TETile SKIN_LIGHT = new TETile('░', new Color(245, 220, 177), Color.black, "light skin");
    public static final TETile SKIN_MID = new TETile('▒', new Color(210, 180, 140), Color.black, "medium skin");
    public static final TETile SKIN_DARK = new TETile('▓', new Color(160, 130, 98), Color.black, "dark skin");
    public static final TETile HAIR = new TETile('█', new Color(101, 67, 33), Color.black, "hair");
    public static final TETile DRESS_DARK = new TETile('■', new Color(40, 40, 60), Color.black, "dark dress");
    public static final TETile DRESS_MID = new TETile('▪', new Color(60, 60, 80), Color.black, "medium dress");
    public static final TETile BACKGROUND = new TETile('~', new Color(120, 140, 100), Color.black, "landscape");
    public static final TETile EYES = new TETile('●', Color.black, Color.black, "eyes");
    public static final TETile SMILE = new TETile('‾', new Color(180, 120, 100), Color.black, "smile");

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        // 初始化背景 - 就像我画布上的远山
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (y > 35) {
                    world[x][y] = BACKGROUND;
                } else {
                    world[x][y] = Tileset.NOTHING;
                }
            }
        }

        // 绘制蒙娜丽莎的轮廓 - 从上到下
        drawMonaLisa(world);

        ter.renderFrame(world);
    }

    private static void drawMonaLisa(TETile[][] world) {
        // 头发轮廓
        drawHair(world);

        // 面部
        drawFace(world);

        // 五官
        drawFeatures(world);

        // 服装
        drawDress(world);
    }

    private static void drawHair(TETile[][] world) {
        // 头发的波浪形状 - 我著名的sfumato技法的数字版本
        for (int y = 30; y < 45; y++) {
            for (int x = 12; x < 28; x++) {
                if (isHairArea(x, y)) {
                    world[x][y] = HAIR;
                }
            }
        }
    }

    private static void drawFace(TETile[][] world) {
        // 脸部椭圆形
        for (int y = 20; y < 35; y++) {
            for (int x = 15; x < 25; x++) {
                if (isFaceArea(x, y)) {
                    // 使用不同深浅模拟光影
                    if (x < 18) {
                        world[x][y] = SKIN_DARK;  // 阴影面
                    } else if (x < 22) {
                        world[x][y] = SKIN_MID;   // 中间调
                    } else {
                        world[x][y] = SKIN_LIGHT; // 受光面
                    }
                }
            }
        }
    }

    private static void drawFeatures(TETile[][] world) {
        // 著名的眼睛 - 那种神秘的凝视
        world[17][28] = EYES;  // 左眼
        world[22][28] = EYES;  // 右眼

        // 那个永恒的微笑
        world[18][24] = SMILE;
        world[19][24] = SMILE;
        world[20][24] = SMILE;
        world[21][24] = SMILE;

        // 鼻子的暗示
        world[19][26] = SKIN_DARK;
    }

    private static void drawDress(TETile[][] world) {
        // 服装的深色调
        for (int y = 10; y < 22; y++) {
            for (int x = 13; x < 27; x++) {
                if (isDressArea(x, y)) {
                    if (y > 15) {
                        world[x][y] = DRESS_DARK;
                    } else {
                        world[x][y] = DRESS_MID;
                    }
                }
            }
        }

        // 手部的暗示
        world[14][15] = SKIN_MID;
        world[15][14] = SKIN_MID;
        world[24][15] = SKIN_MID;
        world[25][14] = SKIN_MID;
    }

    private static boolean isHairArea(int x, int y) {
        // 创建波浪状的头发轮廓
        int centerX = 20;
        int radiusX = 8;
        int radiusY = 7;
        double dx = x - centerX;
        double dy = y - 37;
        return (dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY) <= 1.0;
    }

    private static boolean isFaceArea(int x, int y) {
        // 椭圆形脸部
        int centerX = 20;
        int centerY = 27;
        int radiusX = 5;
        int radiusY = 7;
        double dx = x - centerX;
        double dy = y - centerY;
        return (dx * dx) / (radiusX * radiusX) + (dy * dy) / (radiusY * radiusY) <= 1.0;
    }

    private static boolean isDressArea(int x, int y) {
        // 服装的梯形轮廓
        int topWidth = 8;
        int bottomWidth = 14;
        int centerX = 20;
        int topY = 22;
        int bottomY = 10;

        if (y < bottomY || y > topY) return false;

        double ratio = (double)(y - bottomY) / (topY - bottomY);
        int widthAtY = (int)(bottomWidth + ratio * (topWidth - bottomWidth));

        return Math.abs(x - centerX) <= widthAtY / 2;
    }
}