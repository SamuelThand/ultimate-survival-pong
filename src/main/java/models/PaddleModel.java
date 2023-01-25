package models;

import constants.Constants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The abstract model of a paddle. Contains all information about a paddle existing in the application.
 *
 * @author Samuel Thand
 */
public class PaddleModel {
    private int x;
    private int y;
    private int width;
    private int height;
    private int yVelocity;
    private final int paddleSpeed;
    private final PongModel model;

    /**
     * Constructor. Initializes instance fields.
     *
     * @param model The model of the game.
     */
    public PaddleModel(final PongModel model) {
        this.model = model;
        this.paddleSpeed = Constants.PADDLE_SPEED;
    }

    /**
     * Get the X of this paddle.
     *
     * @return X of this paddle.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the Y of this paddle.
     *
     * @return Y of this paddle.
     */
    public int getY() {
        return y;
    }

    /**
     * Get the width of this paddle.
     *
     * @return width of this paddle.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of this paddle.
     *
     * @return height of this paddle.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the speed of this paddle.
     *
     * @return speed of this paddle.
     */
    public int getPaddleSpeed() {
        return this.paddleSpeed;
    }

    /**
     * Get the dimensions and position of this paddle as a list.
     *
     * @return The dimensions and position of this paddle.
     */
    public ArrayList<Integer> getDimensionsAndPosition() {
        return new ArrayList<>(Arrays.asList(this.x, this.y, this.width, this.height));
    }

    /**
     * Set the X of this paddle.
     *
     * @param x The new X.
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * Set the Y of this paddle.
     *
     * @param y The new Y.
     */
    public void setY(final int y) {
        this.y = y;
    }

    /**
     * Set the height of this paddle.
     *
     * @param height The new height.
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Set the width of this paddle.
     *
     * @param width The new width.
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Set the Y velocity of this paddle.
     *
     * @param yVelocity The new Y velocity.
     */
    public void setYvelocity(final int yVelocity) {
        this.yVelocity = yVelocity;
    }

    /**
     * Move this paddle.
     */
    public void movePaddle() {
        int nextY = this.y + yVelocity;
        int roof = 0;
        int floor = this.model.getBounds().get(Bound.Y);

        boolean paddleIsMoving = nextY != this.y;
        boolean paddleHitsRoof = nextY < roof;
        boolean paddleHitsFloor = nextY > floor - this.height;

        if (paddleIsMoving) {
            if (paddleHitsRoof) {
                this.y = roof;
            } else if (paddleHitsFloor) {
                this.y = floor - this.height;
            } else {
                this.y = this.y + yVelocity;
            }
        }
    }
}
