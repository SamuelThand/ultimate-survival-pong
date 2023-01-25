package constants;

/**
 * Constants used by the application.
 *
 * @author Samuel Thand
 */
public interface Constants {

    int GAME_TICK_DELAY_MS = 17;

    int BIG_BALL_SIDE_LENGTH = 60;
    int MEDIUM_BALL_SIDE_LENGTH = 40;
    int SMALL_BALL_SIDE_LENGTH = 20;

    int EASY_BALL_RANDOMNESS_FACTOR = 1;
    int HARD_BALL_RANDOMNESS_FACTOR = 2;

    int PADDLE_SPEED = 8;
    int PADDLE_WIDTH_FRACTION = 50;
    int PADDLE_HEIGHT_FRACTION = 4;

    int BALLPOOL_MINIMUM_BALLS = 10;
    int BALLPOOL_NEW_BALLS_BATCH_AMOUNT = 5;

    int TIME_BETWEEN_LEVELS = 15;
    int AMOUNT_OF_EASY_LEVELS = 5;

    int THREAD_POOL_CORE_SIZE = 4;
    int THREAD_POOL_MAX_THREADS = 16;

    int MENU_CHOICE_WRITE_RESULT_AND_DATE = 1;
    int MENU_CHOICE_WRITE_RESULT = 2;
    int MENU_CHOICE_DONT_WRITE = 3;
}
