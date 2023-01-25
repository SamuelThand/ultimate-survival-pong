package models;

import constants.Constants;
import controllers.Observer;
import controllers.writers.AbstractResultWriter;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import models.balls.AbstractBallModel;
import models.balls.EasyBallFactory;
import models.balls.HardBallFactory;

/**
 * The model of the game. Contains all data for the game and executes operations on this data. Acts as the Subject
 * in the Observer design pattern, being observed by the PongController.
 *
 * @author Samuel Thand
 */
public class PongModel implements Subject {

    private int currentLevel;
    private int elapsedSeconds;
    private long gameTimer;
    private boolean hardMode;
    private final int paddleWidth;
    private final int paddleHeight;
    private final int paddle1InitialX;
    private final int paddle2InitialX;
    private final int paddlesInitialY;
    private final Map<Bound, Integer> bounds;
    private final EasyBallFactory easyBallFactory;
    private final HardBallFactory hardBallFactory;
    private final PaddleModel paddle1;
    private final PaddleModel paddle2;
    private final List<AbstractBallModel> balls;
    private final BallPoolService ballPoolService;
    private final ThreadPoolManager threadPoolManager;
    private final List<Future<?>> completedProducerTasks;
    private final ArrayList<Observer> observers;

    /**
     * Constructor. Initializes instance fields, all components needed for the game.
     *
     * @param bounds The width and height for the game.
     */
    public PongModel(final Dimension bounds) {
        this.bounds = Stream.of(
                        new AbstractMap.SimpleImmutableEntry<>(Bound.X, bounds.width),
                        new AbstractMap.SimpleImmutableEntry<>(Bound.Y, bounds.height))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        this.paddleWidth = this.bounds.get(Bound.X) / Constants.PADDLE_WIDTH_FRACTION;
        this.paddleHeight = this.bounds.get(Bound.Y) / Constants.PADDLE_HEIGHT_FRACTION;
        this.paddle1InitialX = 0;
        this.paddle2InitialX = this.bounds.get(Bound.X) - this.paddleWidth;
        this.paddlesInitialY = this.bounds.get(Bound.Y) / 2 - this.paddleHeight / 2;
        this.paddle1 = new PaddleModel(this);
        this.paddle2 = new PaddleModel(this);
        this.balls = Collections.synchronizedList(new LinkedList<>());
        this.ballPoolService = new BallPoolService(this);
        this.completedProducerTasks = new LinkedList<>();
        this.threadPoolManager = new ThreadPoolManager(this.completedProducerTasks);
        this.easyBallFactory = new EasyBallFactory(this);
        this.hardBallFactory = new HardBallFactory(this);
        this.observers = new ArrayList<>();
    }

    /**
     * Sets the initial state of the game. If balls are being created, waits for them to be completed
     * before adding to game.
     */
    public void setInitialState() {
        resetState();
        if (ensureBallSupply()) {
            awaitProducerFutures();
        }
        addBallsToGame();
    }

    /**
     * Moves the paddles to their starting positions, resets the level to 1 and
     * disables hard mode.
     */
    private void resetState() {
        this.paddle1.setX(paddle1InitialX);
        this.paddle1.setY(paddlesInitialY);
        this.paddle1.setWidth(this.paddleWidth);
        this.paddle1.setHeight(this.paddleHeight);

        this.paddle2.setX(paddle2InitialX);
        this.paddle2.setY(paddlesInitialY);
        this.paddle2.setWidth(this.paddleWidth);
        this.paddle2.setHeight(this.paddleHeight);

        this.currentLevel = 1;
        this.hardMode = false;
    }

    /**
     * Ensures that there is a minimum amount of balls of each type in the ball pools.
     *
     * @return If any producer tasks were issued.
     */
    private boolean ensureBallSupply() {
        AtomicBoolean tasksWereIssued = new AtomicBoolean(false);
        ballPoolService.getAmountOfAvailableBalls().forEach((ballType, pool) -> {
            if (pool < Constants.BALLPOOL_MINIMUM_BALLS) {
                for (int i = 0; i < Constants.BALLPOOL_NEW_BALLS_BATCH_AMOUNT; i++) {
                    threadPoolManager.execute(new ProducerTask(ballPoolService, ballType));
                }

                if (!tasksWereIssued.get()) {
                    tasksWereIssued.set(true);
                }
            }
        });

        return tasksWereIssued.get();
    }

    /**
     * Waits for all producer tasks to be completed.
     */
    private void awaitProducerFutures() {
        for (Future<?> future : completedProducerTasks) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds two hard balls to the game if hardMode, else adds an easy ball to the game.
     */
    private void addBallsToGame() {
        if (this.hardMode) {
            this.hardBallFactory.setLevel(this.currentLevel);
            balls.add(this.hardBallFactory.createBallOfRandomSize());
            balls.add(this.hardBallFactory.createBallOfRandomSize());
        } else {
            this.easyBallFactory.setLevel(this.currentLevel);
            balls.add(this.easyBallFactory.createBallOfRandomSize());
        }
    }

    /**
     * Moves the balls in the game.
     */
    public void moveBalls() {
        for (AbstractBallModel ball : this.balls) {
            ball.moveBall();
        }
    }

    /**
     * Move the paddles in the game.
     */
    public void movePaddles() {
        this.paddle1.movePaddle();
        this.paddle2.movePaddle();
    }

    /**
     * Checks if it is the next level, and calls nextLevel() if true.
     */
    public void checkIfNextLevel() {
        boolean isNextLevel = this.elapsedSeconds != 0 && this.elapsedSeconds % Constants.TIME_BETWEEN_LEVELS == 0;
        if (isNextLevel) {
            nextLevel();
        }
    }

    /**
     * Increments the level, enables hard mode if the conditions are correct, calls ensureBallSupply() and
     * addBallsToGame().
     */
    private void nextLevel() {
        this.currentLevel++;

        boolean shouldBeHardMode = this.currentLevel > Constants.AMOUNT_OF_EASY_LEVELS && !this.hardMode;
        if (shouldBeHardMode) {
            this.hardMode = true;
        }

        ensureBallSupply();
        addBallsToGame();
    }

    /**
     * Returns all missed to the ballPoolService.
     */
    public void returnMissedBallsToPool() {
        ArrayList<AbstractBallModel> missedBalls = collectMissedBalls();
        balls.removeAll(missedBalls);
        ballPoolService.returnBalls(missedBalls);
    }

    /**
     * Collects all missed balls into an ArrayList.
     *
     * @return The missed balls
     */
    private ArrayList<AbstractBallModel> collectMissedBalls() {
        return this.balls.stream()
                .filter(AbstractBallModel::wasMissed)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Marks the starting time of the game and stores it in gameTimer.
     */
    public void startGameTimer() {
        this.gameTimer = System.currentTimeMillis();
    }

    /**
     * Updates the elapsed seconds, and notifies observers if a whole second has passed.
     */
    public void updateElapsedSeconds() {
        int currentSeconds = (int) calculateElapsedSeconds();
        if (this.elapsedSeconds != currentSeconds) {
            this.elapsedSeconds = currentSeconds;
            notifyObservers();
        }
    }

    /**
     * Calculates the elapsed seconds since the game was started.
     *
     * @return The elapsed seconds.
     */
    private long calculateElapsedSeconds() {
        return (System.currentTimeMillis() - gameTimer) / 1000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param observer The Observer to add to this Subject.
     */
    @Override
    public void addObserver(final Observer observer) {
        this.observers.add(observer);
    }

    /**
     * {@inheritDoc}
     *
     * @param observer The Observer to remove from this Subject.
     */
    @Override
    public void removeObserver(final Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Writes the result of a game to a file.
     *
     * @param resultWriter The resultWriter used for writing the results.
     */
    public void writeResult(final AbstractResultWriter resultWriter) {
        try {
            resultWriter.write(this.currentLevel, this.elapsedSeconds);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the elapsed seconds of the game.
     *
     * @return The elapsed seconds.
     */
    public long getElapsedSeconds() {
        return this.elapsedSeconds;
    }

    /**
     * Get the current level of the game.
     *
     * @return The current level
     */
    public int getCurrentLevel() {
        return this.currentLevel;
    }

    /**
     * Check if it is game over.
     *
     * @return It is game over.
     */
    public boolean isGameOver() {
        return balls.isEmpty();
    }

    /**
     * Get paddle1 of the game.
     *
     * @return paddle1.
     */
    public PaddleModel getPaddle1() {
        return paddle1;
    }

    /**
     * Get paddle2 of the game.
     *
     * @return paddle1.
     */
    public PaddleModel getPaddle2() {
        return paddle2;
    }

    /**
     * Get the balls in the game.
     *
     * @return The balls in the game.
     */
    public List<AbstractBallModel> getBalls() {
        return balls;
    }

    /**
     * Get the threadPoolManager.
     *
     * @return The threadPoolManager.
     */
    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

    /**
     * Get the bounds of the game.
     *
     * @return The bounds.
     */
    public Map<Bound, Integer> getBounds() {
        return bounds;
    }

    /**
     * Get the ballPoolService of the game.
     *
     * @return The ballPoolService.
     */
    public BallPoolService getBallPoolService() {
        return ballPoolService;
    }
}
