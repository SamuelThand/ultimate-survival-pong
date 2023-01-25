package controllers.writers;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Concrete implementation of AbstractResultWriter. Defines an implementation of constructResultString.
 * Is a part of the implementation of the template method design pattern.
 *
 * @author Samuel Thand
 */
public class ResultAndDateWriter extends AbstractResultWriter {

    /**
     * {@inheritDoc}
     * Contains an implementation of the Streams API.
     */
    @Override
    String constructResultString(final int level, final int time) {
        String date = LocalDate.now().toString()
                .chars()
                .mapToObj(character -> (char) character)
                .map(character -> character.equals('-') ? '_' : character)
                .map(Object::toString)
                .collect(Collectors.joining());

        return date + " Result: Level " + level + " Time survived: " + time + " seconds";
    }
}
