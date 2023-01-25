package models;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import models.balls.AbstractBallModel;
import models.balls.EasyBigBallModel;
import models.balls.EasyMediumBallModel;
import models.balls.EasySmallBallModel;
import models.balls.HardBigBallModel;
import models.balls.HardMediumBallModel;
import models.balls.HardSmallBallModel;

/**
 * Ball retainer service. Keeps initialized ball objects in pools, ready for use in the game. Provides
 * a public interface for consuming, producing and returning balls into the pools. Is an implementation of the
 * Object Pool design pattern, and the ball pools are a part of the producer/consumer design pattern.
 *
 * @author Samuel Thand
 */
public class BallPoolService {

    private final PongModel model;
    private final HashMap<Class<? extends AbstractBallModel>, LinkedBlockingQueue<? extends AbstractBallModel>> ballPools;

    /**
     * Constructor. Initializes instance fields and the ball pools using the insertBallPool() method.
     *
     * @param model The model of the game.
     */
    public BallPoolService(final PongModel model) {
        this.model = model;
        this.ballPools = new HashMap<>();
        insertBallPool(EasySmallBallModel.class, new LinkedBlockingQueue<>());
        insertBallPool(EasyMediumBallModel.class, new LinkedBlockingQueue<>());
        insertBallPool(EasyBigBallModel.class, new LinkedBlockingQueue<>());
        insertBallPool(HardSmallBallModel.class, new LinkedBlockingQueue<>());
        insertBallPool(HardMediumBallModel.class, new LinkedBlockingQueue<>());
        insertBallPool(HardBigBallModel.class, new LinkedBlockingQueue<>());
    }

    /**
     * Inserts a ball pool into the ballPools field. Ensures that the Class-key, and the belonging
     * LinkedBlockingQueue elements are of the same type.
     *
     * @param key The class of the ball, acts as key in a hashmap.
     * @param pool The ball pool, acts as a value in a hashmap.
     * @param <T> The type of ball to store in the pool-map.
     */
    private <T extends AbstractBallModel> void insertBallPool(final Class<T> key, final LinkedBlockingQueue<T> pool) {
        this.ballPools.put(key, pool);
    }

    /**
     * Get a ball of the desired type from the ball pools.
     *
     * @param desiredBallType The class of the desired ball.
     * @return The requested ball.
     */
    public AbstractBallModel consumeBall(final Class<? extends AbstractBallModel> desiredBallType) {
        var poolWithDesiredType = this.ballPools.get(desiredBallType);

        return poolWithDesiredType.poll();
    }

    /**
     * Produce a new ball of the desired type, and place it in its belonging ball pool.
     *
     * @param desiredBallType The class of the desired ball.
     * @param <T> The type of the ball to produce.
     * @throws InvocationTargetException If the constructor of the ball type throws an underlying exception.
     * @throws InstantiationException If the desiredBallType cannot be instantiated.
     * @throws IllegalAccessException If this method does not have access to the definition of the desiredBallType class,
     * its constructor or any of its fields.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractBallModel> void produceBall(final Class<T> desiredBallType) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        BlockingQueue<T> ballPool = (BlockingQueue<T>) this.ballPools.get(desiredBallType);
        var newBall = desiredBallType.getConstructors()[0].newInstance(this.model);

        ballPool.add(desiredBallType.cast(newBall));
    }

    /**
     * Return balls to their respective ball pool.
     *
     * @param ballsToReturn The balls to return to a ball pool.
     * @param <T> The type of the ball being returned to a ball pool.
     */
    @SuppressWarnings("unchecked")
    public <T extends  AbstractBallModel> void returnBalls(final ArrayList<AbstractBallModel> ballsToReturn) {
        for (AbstractBallModel ball : ballsToReturn) {
            BlockingQueue<T> ballPool = (BlockingQueue<T>) ballPools.get(ball.getClass());
            ballPool.add((T) ball);
        }
    }

    /**
     * Check if the pool for a certain ball type is empty.
     *
     * @param ballType The type of ball that the pool contains.
     * @param <T> The type of ball that the pool contains.
     * @return The ball pool is empty.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractBallModel> boolean isBallPoolEmpty(final Class<T> ballType) {
        BlockingQueue<T> ballPool = (BlockingQueue<T>) this.ballPools.get(ballType);

        return ballPool.size() == 0;
    }

    /**
     * Get the amount of available balls of each type.
     *
     * @return The type of the ball, and the amount existing.
     */
    public Map<Class<? extends AbstractBallModel>, Integer> getAmountOfAvailableBalls() {
        return ballPools.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }
}
