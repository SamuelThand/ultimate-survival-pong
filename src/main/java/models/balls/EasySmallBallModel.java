package models.balls;

import constants.Constants;
import models.PongModel;

/**
 * Concrete implementation of the model of a ball.
 */
public class EasySmallBallModel extends AbstractBallModel {

    /**
     * Constructor. Uses superclass constructor and initializes instance fields.
     *
     * @param model The model of the game.
     */
    public EasySmallBallModel(final PongModel model) {
        super(model);
        this.sideLength = Constants.SMALL_BALL_SIDE_LENGTH;
        this.randomnessFactor = Constants.EASY_BALL_RANDOMNESS_FACTOR;
    }
}
