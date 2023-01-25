package models.balls;

import java.util.concurrent.ExecutionException;
import models.ConsumerTask;
import models.PongModel;

/**
 * Concrete implementation of the AbstractBallFactory, creates balls. Defines an implementation for the abstract ball creation methods
 * in AbstractBallFactory. Is a part of the implementation of the Abstract Factory, and Factory Method
 * design patterns.
 *
 * @author Samuel Thand
 */
@SuppressWarnings("BusyWait")
public class HardBallFactory extends AbstractBallFactory {

    /**
     * Constructor. Makes use of the superclass constructor.
     *
     * @param model The game model
     */
    public HardBallFactory(final PongModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     *
     * @return A hard big ball
     */
    @Override
    protected HardBigBallModel createBigBall() {
        int xVelocity = this.level * (random.nextBoolean() ? 1 : -1);
        int yVelocity = this.level * (random.nextBoolean() ? 1 : -1);

        while (ballPoolService.isBallPoolEmpty(HardBigBallModel.class)) {
            try {
                Thread.sleep(this.waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var futureBall = threadPoolManager.executeCallable(new ConsumerTask(this.ballPoolService, HardBigBallModel.class));

        HardBigBallModel ball = null;
        try {
            ball = (HardBigBallModel) futureBall.get();
            ball.setX(this.middleX);
            ball.setY(this.middleY);
            ball.setXvelocity(xVelocity);
            ball.setYvelocity(yVelocity);
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            e.printStackTrace();
        }

        return ball;
    }

    /**
     * {@inheritDoc}
     *
     * @return A hard medium ball
     */
    @Override
    protected HardMediumBallModel createMediumBall() {
        int xVelocity = this.level * (random.nextBoolean() ? 1 : -1);
        int yVelocity = this.level * (random.nextBoolean() ? 1 : -1);

        while (ballPoolService.isBallPoolEmpty(HardMediumBallModel.class)) {
            try {
                Thread.sleep(this.waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var futureBall = threadPoolManager.executeCallable(new ConsumerTask(this.ballPoolService, HardMediumBallModel.class));

        HardMediumBallModel ball = null;
        try {
            ball = (HardMediumBallModel) futureBall.get();
            ball.setX(this.middleX);
            ball.setY(this.middleY);
            ball.setXvelocity(xVelocity);
            ball.setYvelocity(yVelocity);
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            e.printStackTrace();
        }

        return ball;
    }

    /**
     * {@inheritDoc}
     *
     * @return A hard small ball
     */
    @Override
    protected HardSmallBallModel createSmallBall() {
        int xVelocity = this.level * (random.nextBoolean() ? 1 : -1);
        int yVelocity = this.level * (random.nextBoolean() ? 1 : -1);

        while (ballPoolService.isBallPoolEmpty(HardSmallBallModel.class)) {
            try {
                Thread.sleep(this.waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var futureBall = threadPoolManager.executeCallable(new ConsumerTask(this.ballPoolService, HardSmallBallModel.class));

        HardSmallBallModel ball = null;
        try {
            ball = (HardSmallBallModel) futureBall.get();
            ball.setX(this.middleX);
            ball.setY(this.middleY);
            ball.setXvelocity(xVelocity);
            ball.setYvelocity(yVelocity);
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            e.printStackTrace();
        }

        return ball;
    }
}
