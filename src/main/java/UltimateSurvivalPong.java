import controllers.PongController;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import models.PongModel;
import views.GameFrame;

/**
 * Ultimate Survival Pong.
 *
 * @author Samuel Thand
 */
final class UltimateSurvivalPong {

    /**
     * Constructor.
     */
    private UltimateSurvivalPong() { }

    /**
     * The starting point of Ultimate Survival Pong.
     * Instantiates the model, view and controller, and displays the GUI.
     *
     * @param args Java command line arguments.
     */
    public static void main(final String[] args) {

        Dimension bounds = determineFrameSize();

        GameFrame view = new GameFrame(bounds);
        PongModel model = new PongModel(bounds);
        new PongController(model, view);

        EventQueue.invokeLater(() -> {
            view.pack();
            view.setLocationRelativeTo(null);
            view.setVisible(true);
        });
    }

    /**
     * Determines the size for the frame, and assigns it to frameSize member.
     *
     * @return Dimension containing the frame size.
     */
    private static Dimension determineFrameSize() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        return new Dimension(screenSize.width / 2, screenSize.height / 2);
    }
}
