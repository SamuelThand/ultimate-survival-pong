package models.balls;

import java.util.Random;
import models.BallPoolService;
import models.Bound;
import models.PongModel;
import models.ThreadPoolManager;

/**
 * A factory that creates AbstractBallModels for the game. Provides an implementation for setting the level of the factory,
 * and randomizing ball creation. Defers implementations of the actual ball creation methods, allowing concrete subclasses
 * to create multiple balls of the same variant. Is a part of the implementation of the Abstract Factory, and Factory Method
 * design patterns.
 *
 * @author Samuel Thand
 */
public abstract class AbstractBallFactory {

   protected final PongModel model;
   protected int level;
   protected int waitInterval;
   protected final Random random;
   protected final int middleX;
   protected final int middleY;
   protected final BallPoolService ballPoolService;
   protected final ThreadPoolManager threadPoolManager;

   /**
    * Constructor. Initializes instance fields. Calculates the starting positions of the balls,
    * middleX and middleY, and gets the BallPoolService and ThreadPool from the model.
    *
    * @param model The game model
    */
   public AbstractBallFactory(final PongModel model) {
      this.model = model;
      this.level = 1;
      this.waitInterval = 1000;
      this.random = new Random();
      this.middleX = this.model.getBounds().get(Bound.X) / 2;
      this.middleY = this.model.getBounds().get(Bound.Y) / 2;
      this.ballPoolService = this.model.getBallPoolService();
      this.threadPoolManager = this.model.getThreadPoolManager();
   }

   /**
    * Set the level of this factory.
    *
    * @param level level of this factory.
    */
   public void setLevel(final int level) {
      this.level = level;
   }

   /**
    * Creates a ball of a random size.
    *
    * @return A ball of a random size.
    */
   public AbstractBallModel createBallOfRandomSize() {
      return switch (random.nextInt(3)) {
         case 0 -> createBigBall();
         case 1 -> createMediumBall();
         case 2 -> createSmallBall();
         default -> throw new IllegalStateException("Unexpected value: " + random.nextInt(2));
      };
   }

   /**
    * Creates a big ball.
    *
    * @return A big ball.
    */
   protected abstract AbstractBallModel createBigBall();

   /**
    * Creates a medium ball.
    *
    * @return A medium ball.
    */
   protected abstract AbstractBallModel createMediumBall();

   /**
    * Creates a small ball.
    *
    * @return A medium ball.
    */
   protected abstract AbstractBallModel createSmallBall();
}
