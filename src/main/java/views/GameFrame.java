package views;

import constants.Constants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import models.balls.AbstractBallModel;

/**
 * The view of the application. Provides a graphical interface which takes user input and visualizes the game.
 *
 * @author Samuel Thand
 */
public final class GameFrame extends JFrame {

    private final Dimension frameSize;
    private JPanel gamePanel;
    private JPanel scorePanel;
    private JPanel infoPanel;
    private JPanel levelPanel;
    private JPanel timePanel;
    private JLabel infoText;
    private JLabel level;
    private JLabel time;
    private JLabel levelNumber;
    private JLabel timeNumber;
    private JButton playButton;
    private JCheckBox redundantCheckbox;
    private Rectangle paddle1;
    private Rectangle paddle2;
    private List<AbstractBallModel> balls;
    private final int scorePanelPadding = 20;
    private final int playButtonWidth = 100;
    private final int playButtonHeight = 40;
    private final int playButtonBorderThickness = 5;

    /**
     * Constructor. Initializes instance fields and calls initUI.
     *
     * @param frameSize The dimensions of the frame.
     */
    public GameFrame(final Dimension frameSize) {
        this.frameSize = frameSize;
        this.paddle1 = new Rectangle();
        this.paddle2 = new Rectangle();
        this.balls = new ArrayList<>();
        initUI();
    }

    /**
     * Initializes the UI by calling relevant methods.
     */
    private void initUI() {
        configFrame();
        initializePanels();
        initializeComponents();
        buildFrame();
    }

    /**
     * Sets the frame to be non-resizable, to exit the application on close and its title.
     */
    private void configFrame() {
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Ultimate Survival Pong");
    }

    /**
     * Initializes all JPanels, and configures them.
     */
    private void initializePanels() {
        this.infoPanel = new InfoPanel(this);
        this.infoPanel.setLayout(new FlowLayout());

        this.scorePanel = new ScorePanel(this);
        this.scorePanel.setLayout(new GridLayout(1, 2));
        this.scorePanel.setBorder(BorderFactory.createEmptyBorder(this.scorePanelPadding, 0, 0, 0));

        this.timePanel = new JPanel();
        this.timePanel.setLayout(new BoxLayout(this.timePanel, BoxLayout.Y_AXIS));

        this.levelPanel = new JPanel();
        this.levelPanel.setLayout(new BoxLayout(this.levelPanel, BoxLayout.Y_AXIS));

        this.gamePanel = new GamePanel(this);
        this.gamePanel.setFocusable(true);
        this.gamePanel.requestFocus();
    }

    /**
     * Initializes all JComponents, and configures them.
     */
    private void initializeComponents() {
        this.infoText = new JLabel("<html><p>This is a survival version of the classic pong.<br>"
                + "To survive - you need to stop the balls from exiting the frame.<br>"
                + "If the frame is empty of balls: YOU LOSE!<br><br>"
                + "Controls:<br> Red (Up - W | Down - S)<br> Blue (Up - Up arrow | Down - Down arrow)</p></html>");
        this.playButton = new JButton("Play");
        this.playButton.setPreferredSize(new Dimension(this.playButtonWidth, this.playButtonHeight));
        this.playButton.setBorder(BorderFactory.createLineBorder(Color.black, this.playButtonBorderThickness, true));
        this.playButton.setFocusable(false);
        this.redundantCheckbox = new JCheckBox("I agree to play this game");
        this.redundantCheckbox.setFocusable(false);

        this.time = new JLabel("Time survived");
        this.timeNumber = new JLabel("");
        this.level = new JLabel("Current level");
        this.levelNumber = new JLabel("");

        this.time.setAlignmentX(CENTER_ALIGNMENT);
        this.level.setAlignmentX(CENTER_ALIGNMENT);
        this.timeNumber.setAlignmentX(CENTER_ALIGNMENT);
        this.levelNumber.setAlignmentX(CENTER_ALIGNMENT);
    }

    /**
     * Adds all JComponents to their JPanels, adds all JPanels to the GameFrame.
     */
    private void buildFrame() {
        this.infoPanel.add(this.infoText);
        this.infoPanel.add(this.redundantCheckbox);
        this.infoPanel.add(this.playButton);

        this.scorePanel.add(this.timePanel);
        this.scorePanel.add(this.levelPanel);

        this.timePanel.add(this.time);
        this.timePanel.add(this.timeNumber);
        this.levelPanel.add(this.level);
        this.levelPanel.add(this.levelNumber);

        this.add(this.infoPanel, BorderLayout.NORTH);
        this.add(this.scorePanel, BorderLayout.CENTER);
        this.add(this.gamePanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a Rectangle object for paddle1.
     *
     * @param paddleInfo The X, Y, width and height for the paddle.
     */
    public void createPaddle1(final ArrayList<Integer> paddleInfo) {
        int x = paddleInfo.get(0);
        int y = paddleInfo.get(1);
        int width = paddleInfo.get(2);
        int height = paddleInfo.get(3);

        this.paddle1 = new Rectangle(x, y, width, height);
    }

    /**
     * Creates a Rectangle object for paddle2.
     *
     * @param paddleInfo The X, Y, width and height for the paddle.
     */
    public void createPaddle2(final ArrayList<Integer> paddleInfo) {
        int x = paddleInfo.get(0);
        int y = paddleInfo.get(1);
        int width = paddleInfo.get(2);
        int height = paddleInfo.get(3);

        this.paddle2 = new Rectangle(x, y, width, height);
    }

    /**
     * Updates the positions of the paddles.
     *
     * @param paddle1x X for paddle 1.
     * @param paddle1y Y for paddle 1.
     * @param paddle2x X for paddle 2.
     * @param paddle2y Y for paddle 2.
     */
    public void updatePaddles(final int paddle1x, final int paddle1y, final int paddle2x, final int paddle2y) {
        if (this.paddle1.x != paddle1x) {
            this.paddle1.x = paddle1x;
        }
        if (this.paddle1.y != paddle1y) {
            this.paddle1.y = paddle1y;
        }
        if (this.paddle2.x != paddle2x) {
            this.paddle2.x = paddle2x;
        }
        if (this.paddle2.y != paddle2y) {
            this.paddle2.y = paddle2y;
        }
    }

    /**
     * Update the list of balls active in the game.
     *
     * @param updatedBalls The balls active in the game.
     */
    public void updateBalls(final List<AbstractBallModel> updatedBalls) {
        this.balls = updatedBalls;
    }

    /**
     * Update the game time counter.
     *
     * @param seconds The elapsed seconds in the game.
     */
    public void updateGameTime(final long seconds) {
        this.timeNumber.setText(String.valueOf(seconds));
    }

    /**
     * Update the level counter.
     *
     * @param level The current level.
     */
    public void updateCurrentLevel(final int level) {
        this.levelNumber.setText(String.valueOf(level));
    }

    /**
     * Repaint the game panel.
     */
    public void repaintGamePanel() {
        this.gamePanel.repaint();
    }

    /**
     * Set a listener method for the play button.
     *
     * @param listener The method to execute when play button is pressed.
     */
    public void setPlayButtonListener(final ActionListener listener) {
        this.playButton.addActionListener(listener);
    }

    /**
     * Set a listener method for key events.
     *
     * @param listener The method to execute when keys are pressed.
     */
    public void setKeyListener(final KeyListener listener) {
        this.gamePanel.addKeyListener(listener);
    }

    /**
     * Set if the play button is clickable.
     *
     * @param condition The button is clickable.
     */
    public void setPlayButtonClickable(final boolean condition) {
        this.playButton.setEnabled(condition);
    }

    /**
     * Get the size of the frame.
     *
     * @return The frame size.
     */
    public Dimension getFrameSize() {
        return frameSize;
    }

    /**
     * Get the rectangle for paddle1.
     *
     * @return paddle1
     */
    public Rectangle getPaddle1() {
        return this.paddle1;
    }

    /**
     * Get the rectangle for paddle2.
     *
     * @return paddle2
     */
    public Rectangle getPaddle2() {
        return this.paddle2;
    }

    /**
     * Get the list of balls.
     *
     * @return The list of balls.
     */
    public List<AbstractBallModel> getBalls() {
        return balls;
    }

    /**
     * Display the Game Over message.
     *
     * @return The menu choice of the user.
     */
    public int displayGameOverMessage() {
        Object[] possibleValues = {
                "Write result and date",
                "Write result",
                "Dont write result"
        };

        Object selectedValue = JOptionPane.showInputDialog(null,
                "You lose! Write the result to a file?", "GAME OVER",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);

        if (selectedValue == null) {
            return 0;
        } else {
            return switch (selectedValue.toString()) {
                case "Write result and date" -> Constants.MENU_CHOICE_WRITE_RESULT_AND_DATE;
                case "Write result" -> Constants.MENU_CHOICE_WRITE_RESULT;
                case "Dont write result" -> Constants.MENU_CHOICE_DONT_WRITE;
                default -> throw new IllegalStateException("Unexpected value: " + selectedValue);
            };
        }
    }
}
