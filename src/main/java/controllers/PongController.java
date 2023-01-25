package controllers;

import constants.Constants;
import controllers.writers.ResultAndDateWriter;
import controllers.writers.ResultWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.Timer;
import models.PaddleModel;
import models.PongModel;
import views.GameFrame;

/**
 * The controller of the program. Controls execution of operations in the model, and updates the graphic view.
 * All communication between the front and backend passes through this controller. Acts as the observer in the Observer
 * design pattern, which observes the subject PongModel.
 *
 * @author Samuel Thand
 */
public final class PongController extends KeyAdapter implements ActionListener, Observer {

    private final PongModel model;
    private final GameFrame view;
    private final Timer actionTimer;
    private boolean wIsPressed;
    private boolean sIsPressed;
    private boolean upIsPressed;
    private boolean downIsPressed;

    /**
     * Constructor. Takes the model and view for the application, and assigns it to the corresponding fields.
     * Instantiates a new Swing timer and assigns this class as listener.
     * Passes itself as a keyListener in the view. Calls the setPLayButtonListener() method.
     *
     * @param model The model of the application
     * @param view The view of the application
     */
    public PongController(final PongModel model, final GameFrame view) {
        this.model = model;
        this.view = view;
        this.actionTimer = new Timer(Constants.GAME_TICK_DELAY_MS, this);
        this.view.setKeyListener(this);

        setPlayButtonListener();
    }

    /**
     * Passes a lambda function calling the startGame() method, as a listener to the play button in the view.
     */
    private void setPlayButtonListener() {
        this.view.setPlayButtonListener((event) -> startGame());
    }

    /**
     * Starts the game. Makes the play button unclickable, calls the setInitialGameState() method, adds this controller as an observer,
     * starts the game timer in the model and starts the Swing timer member.
     */
    private void startGame() {
        this.view.setPlayButtonClickable(false);
        setInitialModelState();
        this.model.addObserver(this);
        this.model.startGameTimer();
        this.actionTimer.start();
    }

    /**
     * Sets the initial game state by calling the model.setInitialState() method, and creating paddles in the view.
     */
    private void setInitialModelState() {
        this.model.setInitialState();
        this.view.createPaddle1(this.model.getPaddle1().getDimensionsAndPosition());
        this.view.createPaddle2(this.model.getPaddle2().getDimensionsAndPosition());
    }

    /**
     * The main game loop triggered by the actionTimer. Calls the relevant methods in the model and view to drive the game.
     *
     * @param gameTick Each tick of the game
     */
    @Override
    public void actionPerformed(final ActionEvent gameTick) {

        if (this.model.isGameOver()) {
            gameOver();
        }

        this.model.updateElapsedSeconds();
        this.model.movePaddles();
        this.model.moveBalls();
        this.model.returnMissedBallsToPool();

        PaddleModel paddle1 = this.model.getPaddle1();
        PaddleModel paddle2 = this.model.getPaddle2();
        this.view.updatePaddles(paddle1.getX(), paddle1.getY(), paddle2.getX(), paddle2.getY());
        this.view.updateBalls(new ArrayList<>(model.getBalls()));
        this.view.repaintGamePanel();
    }

    /**
     * Stops the game loop, resets the state of the model and calls displayLoseMessage().
     */
    private void gameOver() {
        this.actionTimer.stop();
        this.view.setPlayButtonClickable(true);
        this.model.removeObserver(this);
        displayLoseMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        this.model.checkIfNextLevel();
        this.view.updateGameTime(model.getElapsedSeconds());
        this.view.updateCurrentLevel(model.getCurrentLevel());
    }

    /**
     * Calls the displayLoseMessage() method in view, and executes an action depending on the users choice.
     */
    private void displayLoseMessage() {
        switch (view.displayGameOverMessage()) {
            case Constants.MENU_CHOICE_WRITE_RESULT_AND_DATE -> this.model.writeResult(new ResultAndDateWriter());
            case Constants.MENU_CHOICE_WRITE_RESULT -> this.model.writeResult(new ResultWriter());
            default -> { }
        }
    }

    /**
     * Handles key presses from the user, changes the key state instance fields
     * and controls the paddles.
     *
     * @param keyPress The key press
     */
    @Override
    public void keyPressed(final KeyEvent keyPress) {
        PaddleModel paddle1 = this.model.getPaddle1();
        PaddleModel paddle2 = this.model.getPaddle2();

        int keyCode = keyPress.getKeyCode();
        switch (KeyEvent.getKeyText(keyCode)) {
            case "Up" -> {
                this.upIsPressed = true;
                paddle2.setYvelocity(-paddle2.getPaddleSpeed());
            }
            case "Down" -> {
                this.downIsPressed = true;
                paddle2.setYvelocity(paddle2.getPaddleSpeed());
            }
            case "W" -> {
                this.wIsPressed = true;
                paddle1.setYvelocity(-paddle1.getPaddleSpeed());
            }
            case "S" -> {
                this.sIsPressed = true;
                paddle1.setYvelocity(paddle1.getPaddleSpeed());
            }
            default -> { }
        }
    }

    /**
     * Handles key releases from the user, changes the key state instance fields
     * and controls the paddles.
     *
     * @param keyRelease The key release
     */
    @Override
    public void keyReleased(final KeyEvent keyRelease) {
        PaddleModel paddle1 = this.model.getPaddle1();
        PaddleModel paddle2 = this.model.getPaddle2();

        int keyCode = keyRelease.getKeyCode();
        switch (KeyEvent.getKeyText(keyCode)) {
            case "Up" -> {
                this.upIsPressed = false;
                if (!downIsPressed) {
                    paddle2.setYvelocity(0);
                }
            }
            case "Down" -> {
                this.downIsPressed = false;
                if (!upIsPressed) {
                    paddle2.setYvelocity(0);
                }
            }
            case "W" -> {
                this.wIsPressed = false;
                if (!sIsPressed) {
                    paddle1.setYvelocity(0);
                }
            }
            case "S" -> {
                this.sIsPressed = false;
                if (!wIsPressed) {
                    paddle1.setYvelocity(0);
                }
            }
            default -> { }
        }
    }
}
