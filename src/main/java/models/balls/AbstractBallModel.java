package models.balls;

import java.util.Map;
import java.util.Random;
import models.Bound;
import models.PongModel;

/**
 * The abstract model of a ball. Contains all information about a ball existing in the application.
 *
 * @author Samuel Thand
 */
public abstract class AbstractBallModel {

    private int x;
    private int y;
    protected int sideLength;
    protected int randomnessFactor;
    private int yVelocity;
    private int xVelocity;
    private final Map<Bound, Integer> bounds;
    private boolean wasMissed;
    private final PongModel model;
    private final Random random;

    /**
     * Constructor. Initializes instance fields.
     *
     * @param model The model of the game.
     */
    public AbstractBallModel(final PongModel model) {
        this.model = model;
        this.bounds = this.model.getBounds();
        this.random = new Random();
    }

    /**
     * Get the X of this ball.
     *
     * @return X of this ball.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y of this ball.
     *
     * @return Y of this ball.
     */
    public int getY() {
        return y;
    }

    /**
     * Get the sideLength of this ball.
     *
     * @return sideLength of this ball.
     */
    public int getSideLength() {
        return sideLength;
    }

    /**
     * Check if this ball was missed.
     *
     * @return This ball was missed.
     */
    public boolean wasMissed() {
        return wasMissed;
    }

    /**
     * Set the X of this ball.
     *
     * @param x The new X
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Set the Y of this ball.
     *
     * @param y The new y
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Set the X velocity of this ball.
     *
     * @param xVelocity The new X velocity
     */
    public void setXvelocity(final int xVelocity) {
        this.xVelocity = xVelocity;
    }

    /**
     * Set the Y velocity of this ball.
     *
     * @param yVelocity The new Y velocity
     */
    public void setYvelocity(final int yVelocity) {
        this.yVelocity = yVelocity;
    }

    /**
     * Moves this ball.
     */
    public void moveBall() {
        int nextX = this.x + xVelocity;
        int nextY = this.y + yVelocity;

        if (!(this.x == nextX)) {
            this.wasMissed = detectMiss(nextX);
        }

        if (!(this.y == nextY)) {
            detectBounce(nextY);
        }

        detectPaddleHit(nextX, nextY);
    }

    /**
     * Detects if the ball is outside the X bounds, meaning it was missed.
     *
     * @param nextX The next X position for the ball.
     * @return The ball was missed.
     */
    private boolean detectMiss(final int nextX) {
        boolean miss;
        int rightWall = this.bounds.get(Bound.X);

        if (nextX < -this.sideLength) {
            miss = true;
        } else if (nextX > rightWall) {
            miss = true;
        } else {
            setX(this.x + xVelocity);
            miss = false;
        }

        return miss;
    }

    /**
     * Detects if the ball bounces on the top or floor of the game.
     *
     * @param nextY The next Y position for the ball.
     */
    private void detectBounce(final int nextY) {
        int floor = this.bounds.get(Bound.Y);

        if (nextY < 0) {
            setY(0);
            setYvelocity(-this.yVelocity);
            randomiseXvelocity();
        } else if (nextY + this.sideLength > floor) {
            setY(floor - this.sideLength);
            setYvelocity(-this.yVelocity);
            randomiseXvelocity();
        } else {
            setY(this.y + yVelocity);
        }
    }

    /**
     * Randomizes the X velocity of this ball.
     */
    private void randomiseXvelocity() {
        int randomness = (random.nextInt(2) - 1) * this.randomnessFactor;
        setXvelocity(this.xVelocity + randomness);
    }

    /**
     * Detects if the ball has hit a paddle.
     *
     * @param nextX The next X position for the ball.
     * @param nextY The next Y position for the ball.
     */
    private void detectPaddleHit(final int nextX, final int nextY) {
        var paddle1 = this.model.getPaddle1();
        var paddle2 = this.model.getPaddle2();
        int frontOfPaddle1 = paddle1.getX() + paddle1.getWidth();
        int frontOfPaddle2 = paddle2.getX();

        boolean ballIsWithinPaddle1Y = paddle1.getY() - (this.sideLength / 2) <= nextY
                && nextY - (this.sideLength / 2) < (paddle1.getY() + paddle1.getHeight());
        boolean ballIsWithinPaddle2Y = paddle2.getY() - (this.sideLength / 2) <= nextY
                && nextY - (this.sideLength / 2) < (paddle2.getY() + paddle2.getHeight());
        boolean ballHitsPaddle1 = nextX <= frontOfPaddle1 && ballIsWithinPaddle1Y;
        boolean ballHitsPaddle2 = nextX + this.sideLength > frontOfPaddle2 && ballIsWithinPaddle2Y;

        if (ballHitsPaddle1) {
            setX(paddle1.getX() + paddle1.getWidth());
            setXvelocity(-this.xVelocity);
            randomiseYvelocity();
        } else if (ballHitsPaddle2) {
            setX(paddle2.getX() - this.sideLength);
            setXvelocity(-this.xVelocity);
            randomiseYvelocity();
        }
    }

    /**
     * Randomizes the Y velocity of this ball.
     */
    private void randomiseYvelocity() {
        int randomness = (random.nextInt(2) - 1) * this.randomnessFactor;
        setYvelocity(this.yVelocity + randomness);
    }
}
