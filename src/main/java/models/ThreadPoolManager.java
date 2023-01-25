package models;

import constants.Constants;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Manages the thread pools used in the game. Is a part of an implementation of the
 * producer / consumer pattern.
 */
public class ThreadPoolManager implements Executor {

    private final ThreadPoolExecutor producerThreadPool;
    private final ThreadPoolExecutor consumerThreadPool;
    private final List<Future<?>> completedVoidTasks;

    /**
     * Constructor. Creates two ThreadPoolExecutors and initializes completedVoidTasks.
     *
     * @param completedVoidTasks The list where completed tasks are stored.
     */
    public ThreadPoolManager(final List<Future<?>> completedVoidTasks) {
        BlockingQueue<Runnable> producerTaskQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Runnable> consumerTaskQueue = new LinkedBlockingQueue<>();
        this.producerThreadPool = new ThreadPoolExecutor(Constants.THREAD_POOL_CORE_SIZE, Constants.THREAD_POOL_MAX_THREADS,
                1, TimeUnit.SECONDS, producerTaskQueue);
        this.consumerThreadPool = new ThreadPoolExecutor(Constants.THREAD_POOL_CORE_SIZE, Constants.THREAD_POOL_MAX_THREADS,
                1, TimeUnit.SECONDS, consumerTaskQueue);
        this.completedVoidTasks = completedVoidTasks;
    }

    /**
     * {@inheritDoc}
     *
     * Schedule a Runnable task to be executed by a thread pool.
     *
     * @param task The task to be executed by a thread pool.
     */
    @Override
    public void execute(final Runnable task) {
        Future<?> futureCompletion = this.producerThreadPool.submit(task);
        completedVoidTasks.add(futureCompletion);
    }

    /**
     * Schedule a Callable task to be executed by a thread pool.
     *
     * @param task The task to be executed by a thread pool.
     * @return The future result of the task.
     */
    public Future<?> executeCallable(final Callable<?> task) {
        return this.consumerThreadPool.submit(task);
    }
}
