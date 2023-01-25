package models;

import java.lang.reflect.InvocationTargetException;
import models.balls.AbstractBallModel;

/**
 * A task that produces a ball for the BallPoolService. Is a part of the producer/consumer design pattern.
 */
public class ProducerTask implements Runnable {

    private final BallPoolService ballPoolService;
    private final Class<? extends AbstractBallModel> ballType;

    /**
     * Constructor. Initializes instance fields.
     *
     * @param ballPoolService The ballPoolService to consumer a ball from.
     * @param ballType The ball type for this task.
     */
    public ProducerTask(final BallPoolService ballPoolService, final Class<? extends AbstractBallModel> ballType) {
        this.ballPoolService = ballPoolService;
        this.ballType = ballType;
    }

    /**
     * {@inheritDoc}
     *
     * Produces a ball for the ballPoolService
     */
    @Override
    public void run() {
        try {
            this.ballPoolService.produceBall(this.ballType);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
