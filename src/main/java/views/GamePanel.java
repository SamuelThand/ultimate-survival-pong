package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;
import models.balls.AbstractBallModel;

/**
 * The panel containing the game visualization.
 *
 * @author Samuel Thand
 */
public class GamePanel extends JPanel {

    private final GameFrame gameFrame;

    /**
     * Constructor. Initializes instance fields.
     *
     * @param gameFrame The frame of the application.
     */
    GamePanel(final GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    /**
     * Provides a preferred size for the component.
     *
     * @return The preferred size dimension
     */
    @Override
    public Dimension getPreferredSize() {
        return this.gameFrame.getFrameSize();
    }

    /**
     * Defines how the component should paint graphics to the panel.
     */
    @Override
    public void paint(final Graphics g) {
        Dimension frameSize = this.gameFrame.getFrameSize();
        Rectangle paddle1 = this.gameFrame.getPaddle1();
        Rectangle paddle2 = this.gameFrame.getPaddle2();

        g.setColor(Color.white);
        g.fillRect(0, 0, frameSize.width, frameSize.height);

        g.setColor(Color.red);
        g.fillRect(paddle1.x, paddle1.y, paddle1.width, paddle1.height);

        g.setColor(Color.blue);
        g.fillRect(paddle2.x, paddle2.y, paddle2.width, paddle2.height);

        g.setColor(Color.black);
        for (AbstractBallModel ball : this.gameFrame.getBalls()) {
            g.fillOval(ball.getX(), ball.getY(), ball.getSideLength(), ball.getSideLength());
        }
    }
}
