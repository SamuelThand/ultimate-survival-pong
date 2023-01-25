package models;

import java.util.concurrent.Callable;
import models.balls.AbstractBallModel;

/**
 * A task that consumes a ball from the BallPoolService. Is a part of the producer/consumer design pattern.
 */
public class ConsumerTask implements Callable<AbstractBallModel> {

    private final BallPoolService ballPoolService;
    private final Class<? extends AbstractBallModel> ballType;

    /**
     * Constructor. Initializes instance fields.
     *
     * @param ballPoolService The ballPoolService to consumer a ball from.
     * @param ballType The ball type for this task.
     */
    public ConsumerTask(final BallPoolService ballPoolService, final Class<? extends AbstractBallModel> ballType) {
        this.ballPoolService = ballPoolService;
        this.ballType = ballType;
    }

    /**
     * {@inheritDoc}
     *
     * Consumes a ball from the ballPoolService.
     *
     * @return A ball of this ballType.
     */
    @Override
    public AbstractBallModel call() {
        return this.ballPoolService.consumeBall(this.ballType);
    }
}
