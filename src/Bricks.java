import acm.graphics.GCompound;
import acm.graphics.GRect;

import java.awt.*;

/**
 * Created by Микола on 20.04.2017.
 *
 */
public class Bricks extends GCompound{
    private int WIDTH;
    private int HEIGHT;
    private int MARGIN_TOP;
    private int MARGIN_B_BRICKS;
    private GRect brick;

    /**
     *
     * @param marginBricks - distance between bricks (vertical)
     * @param marginTop - distance between bricks (horizontal)
     * @param width - width of a brick
     * @param height - height of a brick
     * @param col - color of a brick
     */
    public Bricks(int marginBricks, int marginTop, int width, int height, Color col){
        this.WIDTH = width;
        this.HEIGHT = height;
        this.MARGIN_TOP = marginTop;
        this.MARGIN_B_BRICKS = marginBricks;
        brick = new GRect(MARGIN_B_BRICKS,MARGIN_TOP,WIDTH,HEIGHT);
        brick.setFilled(true);
        brick.setFillColor(col);
        add(brick);

    }
}
