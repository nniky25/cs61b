package byow.TileEngine;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;

import edu.princeton.cs.introcs.StdDraw; // 普林斯顿大学计算机科学入门课程开发的简化图形绘制库，专门为初学者设计。
import byow.Core.RandomUtils; //  是一个提供便捷随机数生成方法的工具类，用于简化游戏世界生成中的随机选择操作。

/** 双重渲染模式
 * The TETile object is used to represent a single tile in your world. A 2D array of tiles make up a
 * board, and can be drawn to the screen using the TERenderer class.
 *
 * All TETile objects must have a character, textcolor, and background color to be used to represent
 * the tile when drawn to the screen. You can also optionally provide a path to an image file of an
 * appropriate size (16x16) to be drawn in place of the unicode representation（图像渲染 Image-based）.
 * If the image path provided cannot be found, draw will fallback(降级处理) to using the provided
 * character and color representation, so you are free to use image tiles on your own computer.
 *
 * The provided TETile is immutable, i.e. none of its instance variables can change. You are welcome
 * to make your TETile class mutable, if you prefer.
 * 选择不可变的情况：
 *  - 瓦片类型在游戏中固定不变
 *  - 重视线程安全和性能
 *  - 希望避免意外修改
 *
 * 选择可变的情况：
 *  - 需要动态改变瓦片属性
 *  - 实现动画效果或状态变化
 *  - 需要更灵活的游戏机制
 *
 * 实际建议：
 * 对于大多数2D世界生成项目，不可变设计更好
 */

public class TETile {
    private final char character; // Do not rename character or the autograder will break.
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;

    /** 图像模式渲染
     * Full constructor for TETile objects.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over（鼠标悬停在） the tile.
     * @param filepath Full path to image to be used for this tile. Must be correct size (16x16)
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description,
                  String filepath) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
    }

    /** 字符模式渲染
     * Constructor without filepath. In this case, filepath will be null, so when drawing, we
     * will not even try to draw an image, and will instead use the provided character and colors.
     * @param character The character displayed on the screen.
     * @param textColor The color of the character itself.
     * @param backgroundColor The color drawn behind the character.
     * @param description The description of the tile, shown in the GUI on hovering over the tile.
     */
    public TETile(char character, Color textColor, Color backgroundColor, String description) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = null;
    }

    /**
     * Creates a copy of TETile t, except with given textColor.
     * @param t tile to copy
     * @param textColor foreground color for tile copy
     */
    public TETile(TETile t, Color textColor) {
        this(t.character, textColor, t.backgroundColor, t.description, t.filepath);
    }


    /**
     * Draws the tile to the screen at location x, y. If a valid filepath is provided,
     * we draw the image located at that filepath to the screen. Otherwise, we fall
     * back to the character and color representation for the tile.
     *
     * Note that the image provided must be of the right size (16x16). It will not be
     * automatically resized or truncated.
     * @param x x coordinate
     * @param y y coordinate
     */
    public void draw(double x, double y) {
        // 优先使用图像渲染模式
        if (filepath != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, filepath);
                return;
            } catch (IllegalArgumentException e) {
                // Exception happens because the file can't be found. In this case, fail silently
                // and just use the character and background color for the tile.
            }
        }

        // 降级到字符渲染模式
        StdDraw.setPenColor(backgroundColor);
        // Draw background square with current color: center at (x+0.5, y+0.5), half-side length 0.5
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);

        StdDraw.setPenColor(textColor);
        // Display character at the center of the same square
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(character()));
    }

    /** Character representation of the tile. Used for drawing in text mode.
     * @return character representation
     */
    public char character() {
        return character;
    }

    /**
     * Description of the tile. Useful for displaying mouseover text or
     * testing that two tiles represent the same type of thing.
     * @return description of the tile
     */
    public String description() {
        return description;
    }

    /** 自动生成的世界看起来更自然、更有变化，避免 ‘ 复制粘贴 ’ 的人工感。
     * Creates a copy of the given tile with a slightly different text color. The new
     * color will have a red value that is within dr of the current red value,
     * and likewise with dg and db.
     *
     * 实际应用场景
     *  - 地形多样性
     *  - 避免重复感
     *  - 模拟自然变化
     *
     * @param t the tile to copy
     * @param dr the maximum difference in red value
     * @param dg the maximum difference in green value
     * @param db the maximum difference in blue value
     * @param r the random number generator to use（支持随机种子控制）
     */
    public static TETile colorVariant(TETile t, int dr, int dg, int db, Random r) {
        Color oldColor = t.textColor;
        int newRed = newColorValue(oldColor.getRed(), dr, r);
        int newGreen = newColorValue(oldColor.getGreen(), dg, r);
        int newBlue = newColorValue(oldColor.getBlue(), db, r);

        Color c = new Color(newRed, newGreen, newBlue);

        return new TETile(t, c);
    }

    private static int newColorValue(int v, int dv, Random r) {
        // 在原始值v的基础上，加上一个在[-dv, dv]范围内的随机整数，得到一个新的颜色值。
        int rawNewValue = v + RandomUtils.uniform(r, -dv, dv + 1);

        // make sure value doesn't fall outside of the range 0 to 255.
        int newValue = Math.min(255, Math.max(0, rawNewValue));
        return newValue;
    }

    /**
     * Converts the given 2D array to a String. Handy for debugging.
     * Note that since y = 0 is actually the bottom of your world when
     * drawn using the tile rendering engine, this print method has to
     * print in what might seem like backwards order (so that the 0th
     * row gets printed last).
     * @param world the 2D world to print
     * @return string representation of the world
     */
    public static String toString(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        StringBuilder sb = new StringBuilder();

        for (int y = height - 1; y >= 0; y -= 1) {
            for (int x = 0; x < width; x += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                sb.append(world[x][y].character());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Makes a copy of the given 2D tile array.
     * @param tiles the 2D array to copy
     **/
    public static TETile[][] copyOf(TETile[][] tiles) {
        if (tiles == null) {
            return null;
        }

        TETile[][] copy = new TETile[tiles.length][];

        int i = 0;
        for (TETile[] column : tiles) {
            copy[i] = Arrays.copyOf(column, column.length);
            i += 1;
        }

        return copy;
    }
}
