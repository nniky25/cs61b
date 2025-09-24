package byow.lab12;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.HashSet;
import java.util.Set;

/**
 * 测试 HexWorld 类的各个功能
 */
public class HexWorldTest {
    private HexWorld.KeyPoints keyPoints;
    private static final int TEST_SIZE = 3;

    @Before
    public void setUp() {
        keyPoints = new HexWorld.KeyPoints(TEST_SIZE);
    }

    /**
     * 测试 KeyPoints 构造器
     */
    @Test
    public void testKeyPointsConstructor() {
        assertNotNull("KeyPoints 对象不应为null", keyPoints);

        // 测试不同大小的构造器
        HexWorld.KeyPoints small = new HexWorld.KeyPoints(2);
        HexWorld.KeyPoints large = new HexWorld.KeyPoints(5);

        assertNotNull("小尺寸 KeyPoints 不应为null", small);
        assertNotNull("大尺寸 KeyPoints 不应为null", large);
    }

    /**
     * 测试关键点生成
     */
    @Test
    public void testGetKeyPoints() {
        int[][] points = keyPoints.getKeyPoints();

        assertNotNull("关键点数组不应为null", points);
        assertEquals("应该有19个关键点", 19, points.length);

        // 检查每个点都是二维坐标
        for (int i = 0; i < points.length; i++) {
            assertNotNull("第" + i + "个点不应为null", points[i]);
            assertEquals("每个点应该有x,y坐标", 2, points[i].length);
        }
    }

    /**
     * 测试关键点的唯一性（避免重复点）
     */
    @Test
    public void testKeyPointsUniqueness() {
        int[][] points = keyPoints.getKeyPoints();
        Set<String> uniquePoints = new HashSet<>();

        for (int[] point : points) {
            String pointStr = point[0] + "," + point[1];
            assertFalse("发现重复的关键点: " + pointStr,
                    uniquePoints.contains(pointStr));
            uniquePoints.add(pointStr);
        }

        assertEquals("所有关键点应该是唯一的", 19, uniquePoints.size());
    }

    /**
     * 测试关键点坐标的合理性
     */
    @Test
    public void testKeyPointsCoordinatesValidity() {
        int[][] points = keyPoints.getKeyPoints();

        for (int i = 0; i < points.length; i++) {
            int x = points[i][0];
            int y = points[i][1];

            // 坐标不应该为负数
            assertTrue("第" + i + "个点的x坐标不应为负: " + x, x >= 0);
            assertTrue("第" + i + "个点的y坐标不应为负: " + y, y >= 0);

            // 坐标应该在合理范围内
            assertTrue("x坐标应该在合理范围内", x < 1000);
            assertTrue("y坐标应该在合理范围内", y < 1000);
        }
    }

    /**
     * 测试六边形点生成
     */
    @Test
    public void testSaveHexPoints() {
        int x = 10, y = 10, size = 3;
        int[][] hexPoints = HexWorld.saveHexPoints(x, y, size);

        assertNotNull("六边形点数组不应为null", hexPoints);

        // 计算预期的点数: (4*size - 2) * size
        int expectedPoints = (4 * size - 2) * size;
        assertEquals("六边形应该有正确数量的点", expectedPoints, hexPoints.length);

        // 检查所有点的格式
        for (int i = 0; i < hexPoints.length; i++) {
            assertNotNull("第" + i + "个六边形点不应为null", hexPoints[i]);
            assertEquals("每个六边形点应该有x,y坐标", 2, hexPoints[i].length);
        }
    }

    /**
     * 测试六边形点的边界
     */
    @Test
    public void testHexPointsBoundaries() {
        int anchorX = 5, anchorY = 5, size = 2;
        int[][] hexPoints = HexWorld.saveHexPoints(anchorX, anchorY, size);

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (int[] point : hexPoints) {
            minX = Math.min(minX, point[0]);
            maxX = Math.max(maxX, point[0]);
            minY = Math.min(minY, point[1]);
            maxY = Math.max(maxY, point[1]);
        }

        // 验证边界合理性
        assertTrue("最小x应该小于等于锚点x", minX <= anchorX);
        assertTrue("最大x应该大于等于锚点x", maxX >= anchorX);
        assertTrue("最小y应该等于锚点y", minY == anchorY);
        assertTrue("最大y应该大于锚点y", maxY > anchorY);
    }

    /**
     * 测试不同大小六边形的点数计算
     */
    @Test
    public void testHexPointsCountForDifferentSizes() {
        for (int size = 2; size <= 5; size++) {
            int[][] hexPoints = HexWorld.saveHexPoints(0, 0, size);
            int expectedCount = (4 * size - 2) * size;

            assertEquals("大小为" + size + "的六边形应该有" + expectedCount + "个点",
                    expectedCount, hexPoints.length);
        }
    }

    /**
     * 测试随机瓦片生成
     */
    @Test
    public void testRandomTileGeneration() {
        // 由于randomTile()是private方法，我们通过fillWithTiles()间接测试
        TETile[][] world = HexWorld.fillWithTiles(TEST_SIZE);

        assertNotNull("生成的世界不应为null", world);
        assertTrue("世界宽度应该大于0", world.length > 0);
        assertTrue("世界高度应该大于0", world[0].length > 0);

        // 检查世界中有非NOTHING的瓦片
        boolean hasNonEmptyTiles = false;
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] != Tileset.NOTHING) {
                    hasNonEmptyTiles = true;
                    break;
                }
            }
            if (hasNonEmptyTiles) break;
        }

        assertTrue("世界中应该有非空的瓦片", hasNonEmptyTiles);
    }

    /**
     * 测试世界尺寸计算
     */
    @Test
    public void testWorldSizeCalculation() {
        for (int size = 2; size <= 5; size++) {
            TETile[][] world = HexWorld.fillWithTiles(size);

            int expectedWidth = size * 11 - 6;
            int expectedHeight = size * 10;

            assertEquals("大小" + size + "的世界宽度应该是" + expectedWidth,
                    expectedWidth, world.length);
            assertEquals("大小" + size + "的世界高度应该是" + expectedHeight,
                    expectedHeight, world[0].length);
        }
    }

    /**
     * 测试随机大小生成
     */
    @Test
    public void testGetRandomSize() {
        for (int i = 0; i < 100; i++) {
            int size = HexWorld.getRandomSize();
            assertTrue("随机大小应该在2-5范围内", size >= 2 && size <= 5);
        }
    }

    /**
     * 测试世界填充的完整性
     */
    @Test
    public void testWorldFillCompleteness() {
        TETile[][] world = HexWorld.fillWithTiles(3);

        // 统计不同类型瓦片的数量
        int emptyTiles = 0;
        int filledTiles = 0;

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.NOTHING) {
                    emptyTiles++;
                } else {
                    filledTiles++;
                }
            }
        }

        assertTrue("应该有一些空白区域", emptyTiles > 0);
        assertTrue("应该有填充的六边形区域", filledTiles > 0);
    }

    /**
     * 测试边界情况 - 最小尺寸
     */
    @Test
    public void testMinimumSize() {
        // 测试最小尺寸 size = 1 的情况
        HexWorld.KeyPoints smallKeyPoints = new HexWorld.KeyPoints(1);
        int[][] points = smallKeyPoints.getKeyPoints();

        assertNotNull("最小尺寸的关键点不应为null", points);
        assertEquals("最小尺寸也应该有19个关键点", 19, points.length);
    }

    /**
     * 测试颜色变化功能
     */
    @Test
    public void testColorVariantIntegration() {
        // 通过检查生成的世界来间接测试颜色变化
        TETile[][] world = HexWorld.fillWithTiles(2);

        // 收集所有非空瓦片
        Set<String> tileDescriptions = new HashSet<>();
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] != Tileset.NOTHING) {
                    tileDescriptions.add(world[x][y].description());
                }
            }
        }

        assertFalse("应该有一些瓦片被放置", tileDescriptions.isEmpty());
    }

    /**
     * 性能测试 - 确保大尺寸情况下不会过慢
     */
    @Test(timeout = 5000) // 5秒超时
    public void testPerformanceWithLargeSize() {
        TETile[][] world = HexWorld.fillWithTiles(5);
        assertNotNull("大尺寸世界生成不应超时", world);
    }
}
