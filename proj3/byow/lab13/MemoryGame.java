package byow.lab13;

import byow.Core.RandomUtils;
import java.awt.Font;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};
    private static final String[][] WORDS = {
            {"abstract", "抽象的；摘要"},
            {"allocate", "分配；拨给"},
            {"analyze", "分析；分解"},
            {"approach", "方法；接近"},
            {"assess", "评估；估价"},
            {"assume", "假设；承担"},
            {"concept", "概念；思想"},
            {"constitute", "构成；组成"},
            {"contrast", "对比；形成对照"},
            {"contribute", "贡献；捐助"},
            {"derive", "源自；得到"},
            {"distribute", "分发；分配"},
            {"eliminate", "消除；淘汰"},
            {"emerge", "出现；浮现"},
            {"evaluate", "评估；估计"},
            {"expand", "扩展；膨胀"},
            {"expose", "暴露；揭露"},
            {"identify", "识别；确定"},
            {"imply", "暗示；意味着"},
            {"indicate", "表明；指示"},
            {"interpret", "解释；口译"},
            {"maintain", "维持；主张"},
            {"obtain", "获得；得到"},
            {"occur", "发生；出现"},
            {"potential", "潜力；可能的"},
            {"previous", "先前的；以前的"},
            {"promote", "促进；提升"},
            {"relevant", "相关的；切题的"},
            {"significant", "重要的；有意义的"},
            {"tend", "倾向于；照顾"}
    };

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame2();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(1250, 650);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            char randChar = randChar();
            sb.append(randChar);
        }

        String result = sb.toString();
        return result;
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen

        /*// 把板子背景刷新成黑色
        StdDraw.clear(StdDraw.BLACK);
        // 设置字体颜色
        StdDraw.setPenColor(StdDraw.WHITE);
        // 设置动画效果
        StdDraw.enableDoubleBuffering();
        // 绘画
        StdDraw.text(0.5, 0.5, s);
        StdDraw.line(0, 0.95, 1, 0.95);
        StdDraw.show();*/
        // 逐字显示
        for (int i = 0; i <= s.length(); i++) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);

            String substring = s.substring(0, i);  // 逐渐增加显示的字符
            StdDraw.text(width / 2.0, height / 2.0, substring);

            StdDraw.show();
            StdDraw.pause(10);  // ⭐ 暂停100毫秒，产生动画效果
        }
        StdDraw.pause(500);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);
        StdDraw.show();

        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void drawWord(String s) {
        //TODO: Take the string and display it in the center of the screen

        // 逐字显示
        for (int i = 0; i <= s.length(); i++) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);

            String substring = s.substring(0, i);  // 逐渐增加显示的字符
            StdDraw.text(width / 2.0, height / 2.0, substring);

            StdDraw.show();
            StdDraw.pause(10);  // ⭐ 暂停100毫秒，产生动画效果
        }
        StdDraw.pause(3000); // 暂停两秒，供user记忆

        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void drawFameWithPng(String s) {
        //TODO: Take the string and display it in the center of the screen
        // 逐字显示
        for (int i = 0; i <= s.length(); i++) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);

            String substring = s.substring(0, i);  // 逐渐增加显示的字符
            StdDraw.text(width / 2.0, height / 2.0, substring);

            StdDraw.show();
            StdDraw.pause(10);  // ⭐ 暂停100毫秒，产生动画效果
        }

        StdDraw.pause(1000);
        StdDraw.clear(StdDraw.BLACK);
        String filename = "byow/lab13/photo1.png";
        StdDraw.picture(width / 2.0, height / 2.0, filename);
        StdDraw.show();
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        String[] arr = letters.split("");
        for (int i = 0; i < letters.length(); i++) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);
            StdDraw.setPenColor(StdDraw.WHITE);
            // 停顿500毫秒
            StdDraw.pause(500);

            StdDraw.text(width / 2.0,height / 2.0, arr[i]);
            StdDraw.show();
            // 显示500毫秒，产生动画效果
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String input = "";  // 用于存储用户输入的字符串

        // 循环读取n个字符
        while (input.length() < n) {
            // 检查是否有按键被按下
            if (StdDraw.hasNextKeyTyped()) {
                // 获取按下的键
                char key = StdDraw.nextKeyTyped();

                // 检查是否为退格键
                if (key == '\b') {  // '\b' 是退格字符
                    if (input.length() > 0) {
                        input = input.substring(0, input.length() - 1);  // 删除最后一个字符
                    }
                } else {
                    input += key;  // 添加新字符
                }

                // 清空屏幕
                StdDraw.clear(StdDraw.BLACK);
                StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);

                // 设置字体颜色
                StdDraw.setPenColor(StdDraw.WHITE);

                // 在屏幕中央显示当前输入的字符串
                StdDraw.text(width / 2.0,height / 2.0, input);

                // 显示更新
                StdDraw.show();
            }
        }
        return input;
    }

    public void startGame1() {
        //TODO: Set any relevant variables before the game starts
        //TODO: Establish Engine loop
        round = 1;
        gameOver = false;

        while (!gameOver) {
            // 显示回合数
            StdDraw.clear(Color.BLACK);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);
            StdDraw.setPenColor(Color.WHITE);
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            drawFrame("word: " + round);
            //StdDraw.show();
            StdDraw.pause(500);

            // 生成随机字符串
            //String target = generateRandomString(round);
            String enTraget = WORDS[round - 1][0];
            String cnTraget2 = WORDS[round - 1][1];

            // 闪烁显示目标字符串
            //flashSequence(traget);
            drawWord(enTraget + "\n" + cnTraget2);

            // 提示玩家输入
            StdDraw.clear(Color.BLACK);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);
            StdDraw.setPenColor(Color.WHITE);
            drawFrame("Type english word!");
            StdDraw.show();
            StdDraw.pause(100);

            // 获取玩家输入
            String playerInput = solicitNCharsInput(enTraget.length());
            StdDraw.pause(500);

            // 检查是否正确
            if (playerInput.equals(enTraget)) {
                round++;
            } else {
                gameOver = true;

                // 显示游戏结束信息
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font gameOverFont = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(gameOverFont);
                drawFrame("Game Over!");
                //StdDraw.text(width / 2.0, height / 2.0 + 2, "Game Over!");
                drawFameWithPng("You made it to word: " + round);
                //StdDraw.text(width / 2.0, height / 2.0 - 2, "You made it to round: " + round);
                StdDraw.show();
            }
        }
    }

    public void startGame2() {
        //TODO: Set any relevant variables before the game starts
        //TODO: Establish Engine loop
        round = 1;
        gameOver = false;

        while (!gameOver) {
            // 显示回合数
            StdDraw.clear(Color.BLACK);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);
            StdDraw.setPenColor(Color.WHITE);
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            drawFrame("round: " + round);
            //StdDraw.show();
            StdDraw.pause(500);

            // 生成随机字符串
            String target = generateRandomString(round);
            // 闪烁显示目标字符串
            flashSequence(target);

            // 提示玩家输入
            StdDraw.clear(Color.BLACK);
            StdDraw.line(0, height - height / 12.0, width, height - height / 12.0);
            StdDraw.setPenColor(Color.WHITE);
            drawFrame("Type it!");
            StdDraw.show();
            StdDraw.pause(100);

            // 获取玩家输入
            String playerInput = solicitNCharsInput(round);
            StdDraw.pause(500);

            // 检查是否正确
            if (playerInput.equals(target)) {
                round++;
            } else {
                gameOver = true;

                // 显示游戏结束信息
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                Font gameOverFont = new Font("Monaco", Font.BOLD, 30);
                StdDraw.setFont(gameOverFont);
                drawFrame("Game Over!");
                //StdDraw.text(width / 2.0, height / 2.0 + 2, "Game Over!");
                drawFameWithPng("You made it to round: " + round);
                //StdDraw.text(width / 2.0, height / 2.0 - 2, "You made it to round: " + round);
                StdDraw.show();
            }
        }
    }

    public char randChar() {
        int number = rand.nextInt(CHARACTERS.length);
        return CHARACTERS[number];
    }
}
