package controllers.writers;

/**
 * Concrete implementation of AbstractResultWriter. Defines an implementation of constructResultString.
 * Is a part of the implementation of the template method design pattern.
 *
 * @author Samuel Thand
 */
public class ResultWriter extends AbstractResultWriter {

    /**
     * {@inheritDoc}
     */
    @Override
    String constructResultString(final int level, final int time) {
        return "Result: Level " + level + " Time survived: " + time + " seconds";
    }
}
