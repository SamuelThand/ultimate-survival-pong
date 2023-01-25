package controllers.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Provides functionality for writing the results of a game to a text file. Provides an implementation for
 * opening a file stream, and writing of the data. Defers the implementation of constructing a result string to concrete
 * subclasses. Is a part of the implementation of the template method design pattern.
 *
 * @author Samuel Thand
 */
public abstract class AbstractResultWriter {

    private final String filePath;

    /**
     * Constructor. Initializes a file path for writing, and stores it in filePath.
     */
    AbstractResultWriter() {
        this.filePath = System.getProperty("user.dir") + "/_Resources/PongResults.txt";
    }

    /**
     * Writes a string to a file using an output stream.
     *
     * @param level The level reached during the game.
     * @param time The time survived during the game.
     * @throws FileNotFoundException If a file is not found.
     */
    public final void write(final int level, final int time) throws FileNotFoundException {
        var outputStream = openFileOutputStream();
        var resultString = constructResultString(level, time);
        writeData(outputStream, resultString);
    }

    /**
     * Opens a FileOutputStream to filePath, creates required directories if they don't exist.
     *
     * @return The FileOutputStream
     * @throws FileNotFoundException If a file is not found.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    FileOutputStream openFileOutputStream() throws FileNotFoundException {
        File resultsFile = new File(filePath);
        resultsFile.getParentFile().mkdirs();
        return new FileOutputStream(filePath, true);
    }

    /**
     * Construct a result string from the reached level and survived time.
     *
     * @param level The level reached during the game.
     * @param time The time survived during the game.
     * @return The result String
     */
    abstract String constructResultString(int level, int time);

    /**
     * Appends a result String with a newline, converts it to UTF_8 bytes, and calls write() on the outputStream
     * using a dedicated writing Thread.
     *
     * @param outputStream The outputStream used for writing.
     * @param resultString The result String.
     */
    void writeData(final FileOutputStream outputStream, String resultString) {
        resultString = resultString + "\n";
        byte[] resultBytes = resultString.getBytes(StandardCharsets.UTF_8);

        Thread writerThread = new Thread(() -> {
            try {
                outputStream.write(resultBytes);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writerThread.start();
    }
}
