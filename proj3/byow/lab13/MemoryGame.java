package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private int width;
    /**
     * The height of the window of this game.
     */
    private int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private Random rand;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        // Initialize random number generator
        initDraw();

        this.rand = new Random(seed);
        this.round = 1;
        this.gameOver = false;
    }

    private void initDraw() {
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        StringBuilder builder = new StringBuilder();
        //检查n > 0是否为真,如果条件为真，执行循环体
        //后执行n--（n减1）
        while (n-- > 0) {
            builder.append(CHARACTERS[this.rand.nextInt(CHARACTERS.length)]);
        }
        return builder.toString();
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        // If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(this.width / 2.0, this.height - 1.5, "Round " + this.round);
        StdDraw.text(this.width / 2.0, this.height / 2.0, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure to blank the screen between letters
        for (char s : letters.toCharArray()) {
            StdDraw.clear(StdDraw.BLACK);
            drawFrame(String.valueOf(s));
            double start = getTime();
            while (getTime() - start < 1) {
            }
            StdDraw.clear(StdDraw.BLACK);
            drawFrame("");
            start = getTime();
            while (getTime() - start < 0.5) {

            }
        }
    }

    /**
     * @return 当前时间的微秒
     */
    private static double getTime() {
        return LocalTime.now().getSecond() + LocalTime.now().getNano() / 1000000;
    }

    public String solicitNCharsInput(int n) {
        // Read n letters of player input
        StringBuilder builder = new StringBuilder();
        while (n-- > 0) {
            while (!StdDraw.hasNextKeyTyped()) {
            }
            builder.append(StdDraw.nextKeyTyped());
            drawFrame(builder.toString());
        }
        return builder.toString();
    }

    public void startGame() {
        //Set any relevant variables before the game starts
        // Establish Engine loop
        while (!gameOver) {
            String sequence = generateRandomString(round);
            flashSequence(sequence);
            while (StdDraw.hasNextKeyTyped()){
                StdDraw.nextKeyTyped();
            }
            if (!sequence.equals(solicitNCharsInput(sequence.length()))) {
                drawFrame("Game Over!");
                gameOver = true;
            } else {
                double start = getTime();
                while (getTime() - start < 1) {
                }
                drawFrame("√");
                while (getTime() - start < 2) {
                }
            }
            round++;
        }
    }

}
