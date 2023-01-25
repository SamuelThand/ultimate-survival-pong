package models.balls;

import constants.Constants;
import models.PongModel;

/**
 * Concrete implementation of the model of a ball.
 */
public class HardBigBallModel extends AbstractBallModel {

    /**
     * Constructor. Uses superclass constructor and initializes instance fields.
     *
     * @param model The model of the game.
     */
    public HardBigBallModel(final PongModel model) {
        super(model);
        this.sideLength = Constants.BIG_BALL_SIDE_LENGTH;
        this.randomnessFactor = Constants.HARD_BALL_RANDOMNESS_FACTOR;
    }
}
