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
public class EasyBallFactory extends AbstractBallFactory {

    /**
     * Constructor. Makes use of the superclass constructor.
     *
     * @param model The game model
     */
    public EasyBallFactory(final PongModel model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     *
     * @return An easy big ball
     */
    @Override
    protected EasyBigBallModel createBigBall() {
        int xVelocity = this.level * (random.nextBoolean() ? 1 : -1);
        int yVelocity = this.level * (random.nextBoolean() ? 1 : -1);

        while (ballPoolService.isBallPoolEmpty(EasyBigBallModel.class)) {
            try {
                Thread.sleep(this.waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var futureBall = threadPoolManager.executeCallable(new ConsumerTask(this.ballPoolService, EasyBigBallModel.class));

        EasyBigBallModel ball = null;
        try {
            ball = (EasyBigBallModel) futureBall.get();
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
     * @return An easy medium ball
     */
    @Override
    protected EasyMediumBallModel createMediumBall() {
        int xVelocity = this.level * (random.nextBoolean() ? 1 : -1);
        int yVelocity = this.level * (random.nextBoolean() ? 1 : -1);

        while (ballPoolService.isBallPoolEmpty(EasyMediumBallModel.class)) {
            try {
                Thread.sleep(this.waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var futureBall = threadPoolManager.executeCallable(new ConsumerTask(this.ballPoolService, EasyMediumBallModel.class));

        EasyMediumBallModel ball = null;
        try {
            ball = (EasyMediumBallModel) futureBall.get();
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
     * @return An easy small ball
     */
    @Override
    protected EasySmallBallModel createSmallBall() {
        int xVelocity = this.level * (random.nextBoolean() ? 1 : -1);
        int yVelocity = this.level * (random.nextBoolean() ? 1 : -1);

        while (ballPoolService.isBallPoolEmpty(EasySmallBallModel.class)) {
            try {
                Thread.sleep(this.waitInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        var futureBall = threadPoolManager.executeCallable(new ConsumerTask(this.ballPoolService, EasySmallBallModel.class));

        EasySmallBallModel ball = null;
        try {
            ball = (EasySmallBallModel) futureBall.get();
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
