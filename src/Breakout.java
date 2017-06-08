import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;

import acm.program.GraphicsProgram;
import acm.util.MediaTools;
import acm.util.RandomGenerator;

import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by eddie on 27.04.2017.
 */
public class Breakout extends GraphicsProgram {

    /**
     * Width and height of application window in pixels
     */
    private static final int APPLICATION_WIDTH = 400;
    private static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    /**
     * Total number of bricks
     */
    private static final int NBRICKS = 100;
    /**
     * Instance of RandomGenerator
     */
    private RandomGenerator rgen = RandomGenerator.getInstance();

    /**
     * The lowest y-coordinate of the lowest brick
     */
    private static final int LOWPOINT = BRICK_Y_OFFSET + NBRICK_ROWS * BRICK_SEP + NBRICK_ROWS * BRICK_HEIGHT;

    /**
     * Delay
     */
    private static final double DELAY = 10;
    private GOval ball;
    private GRect paddle;
    /**
     * Labels, showing amount of attempts
     */
    private GLabel attempts;
    private GLabel bricksnum;
    /**
     * Velocity
     */

    private double vx;
    private double vy;
    /**
     * Amount of turns left
     */
    private int turns = NTURNS;
    /**
     * Amount of bricks removed
     */
    private int bricks = 0;
    /**
     * Audio for ball collision
     */
    private AudioClip ball_colission= MediaTools.loadAudioClip("1.au");
    /**
     * creates paddle
     */
    private void paddleCreator() {
        paddle = new GRect(APPLICATION_WIDTH / 2, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setFillColor(Color.BLACK);
        add(paddle);
    }

    /**
     * Creates ball
     */
    private void ballCreator() {
        ball = new GOval(APPLICATION_WIDTH / 2 - BALL_RADIUS / 2, APPLICATION_HEIGHT / 2 - BALL_RADIUS / 2, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
        ball.setFilled(true);
        ball.setFillColor(Color.BLACK);
        add(ball);
    }

    /**
     * Creates bricks
     */
    private void bricksCreator() {
        int brickN = 0;
        Bricks brick;
        Color color = Color.RED;
        for (int k = 0, row = 0; row < 10; k++) {
            if (k == 10) {
                k = 0;
                row++;
                if (row == 2)
                    color = Color.ORANGE;
                else if (row == 4)
                    color = Color.YELLOW;
                else if (row == 6)
                    color = Color.GREEN;
                else if (row == 8)
                    color = Color.CYAN;
            }
            if (brickN < 100) {
                brick = new Bricks(BRICK_SEP * k + 1 + k * BRICK_WIDTH, BRICK_Y_OFFSET + row * BRICK_SEP + row * BRICK_HEIGHT, BRICK_WIDTH, BRICK_HEIGHT, color);
                add(brick);
                brickN++;
            }

        }
    }


    public void run() {
        this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
        /* You fill this in, along with any subsidiary methods */
        bricksCreator();
        addMouseListeners();
        paddleCreator();
        setUp();
    }

    /**
     * Is used every new attempt
     */
    private void setUp() {
        printLabelAttempts();
        ballCreator();
        pause(100);
        game();
    }

    /**
     * removes ball, sets velocity to zero, starts the new attempt
     */
    private void newAttempt() {
        turns--;
        remove(attempts);
        remove(bricksnum);
        remove(ball);
        vx =0;
        vy=0;
        setUp();
    }

    /**
     * Sets up velocity, moves, manages labels and ends the game
     */
    private void game() {
        /**
         * if no velocity exist
         */
        if (vx == 0 && vy == 0) {
            vx = rgen.nextDouble(1.0, 2.5);
            if (rgen.nextBoolean(0.5)) {
                vx = -vx;
            }
            vy = 3.0;
            game();
        } else {

            while (turns != 0 && bricks != NBRICKS) {
                printLabelBricks();
                /**
                 * Left wall collision, right wall collision
                 */
                if ((ball.getX() - vx <= 0 && vx < 0) || (ball.getX() + vx >= APPLICATION_WIDTH - BALL_RADIUS * 2 && vx > 0)) {
                    ball_colission.play();
                    vx = -vx;
                }
                /**
                 * Top wall collision
                 */
                if (ball.getY() - vy <= 0 && vy < 0) {
                    ball_colission.play();
                    vy = -vy;
                    vy += rgen.nextDouble(0.15, 0.5);
                    vx += rgen.nextDouble(0.15, 0.5);
                }
                /**
                 * Bottom wall collision
                 */
                if (ball.getY() + 2 * BALL_RADIUS >= APPLICATION_HEIGHT) {
                    ball_colission.play();
                    newAttempt();//new attempt
                }

                GObject coll = getCollidingObj();
                if (coll == paddle) {
                    if (ball.getY() + 2 * BALL_RADIUS >= paddle.getY()) {
                        ball_colission.play();
                        vy = -vy;
                    }

                } else if (coll != null) {
                    if (coll != ball && coll != bricksnum && coll != attempts) {//removes bricks
                        ball_colission.play();
                        remove(coll);
                        bricks++;
                        vy = -vy;
                    }
                }
                ball.move(vx, vy);//moves
                pause(DELAY);
                remove(bricksnum);
            }

            if (bricks == NBRICKS) {
                victory();
                return;
            }
            if (turns == 0) {
                defeat();
            }

        }
    }


    /**
     * checks 4 points: top left, top right, bottom left. bottom right
     *
     * @return colliding obj
     */
    private GObject getCollidingObj() {

        if (getElementAt(ball.getX(), ball.getY()) != null) {//first point (x,y)
            return getElementAt(ball.getX(), ball.getY());
        } else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {//second point (x+2r,y)
            return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
        } else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {//third point(x,y+2*r)
            return getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
        } else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {//fourth point(x+2*r,y+2*r)
            return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
        } else {//no object found
            return null;
        }
    }

    /**
     * Label to show the amount of attempts left
     */
    private void printLabelAttempts() {
        String label1L = "Attempts remaining: ";
        attempts = new GLabel(label1L + turns, 10, 10);
        add(attempts);

    }

    /**
     * Label to show the amount of bricks removed
     */
    private void printLabelBricks() {
        String label2L = "Bricks destroyed: ";
        bricksnum = new GLabel(label2L + bricks, 10, 30);
        add(bricksnum);

    }

    /**
     * In case of victory
     */
    private void victory() {
        remove(paddle);
        remove(ball);
        GLabel label = new GLabel("YOU WON! CONGRATULATIONS!", APPLICATION_WIDTH / 2 - 40, APPLICATION_HEIGHT / 2);
        add(label);
    }

    /**
     * In case of defeat
     */
    private void defeat() {
        remove(paddle);
        remove(ball);
        GLabel label = new GLabel("YOU LOST!", APPLICATION_WIDTH / 2-30,APPLICATION_HEIGHT / 2);
        add(label);
    }

    /**
     * moves paddle
     *
     * @param e mouseListener
     */
    public void mouseMoved(MouseEvent e) {
        double xp = e.getX();
        if (xp >= 0 && xp <= (APPLICATION_WIDTH - PADDLE_WIDTH)) {
            paddle.setLocation(xp, APPLICATION_HEIGHT - PADDLE_Y_OFFSET);
        }

    }


}
