package views;

import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * The panel containing the game score.
 *
 * @author Samuel Thand
 */
public class ScorePanel extends JPanel {

    private final GameFrame gameFrame;
    private final int frameHeightFraction = 5;

    /**
     * Constructor. Initializes instance fields.
     *
     * @param gameFrame The frame of the game.
     */
    ScorePanel(final GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    /**
     * Provides a preferred size for the component.
     *
     * @return The preferred size dimension
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension frameSize = gameFrame.getFrameSize();

        return new Dimension(frameSize.width, frameSize.height / this.frameHeightFraction);
    }
}
