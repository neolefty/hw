package org.neolefty.cs143.hybrid_images.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/** Minimal wrapper around an ExecutorService that catches and prints exceptions
 *  from the runnables and callables. */
public class ThrowablePrintingExecutorService implements ExecutorService {
    private ExecutorService wrapped;

    public ThrowablePrintingExecutorService(ExecutorService wrapped) {
        this.wrapped = wrapped;
    }

    @Override public void shutdown() { wrapped.shutdown(); }
    @Override public List<Runnable> shutdownNow() { return wrapped.shutdownNow(); }
    @Override public boolean isShutdown() { return wrapped.isShutdown(); }
    @Override public boolean isTerminated() { return wrapped.isTerminated(); }
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return wrapped.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        try {
            return wrapped.submit(task);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        try {
            return wrapped.submit(task, result);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        try {
            return wrapped.submit(task);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        try {
            return wrapped.invokeAll(tasks);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return wrapped.invokeAll(tasks, timeout, unit);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        try {
            return wrapped.invokeAny(tasks);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return wrapped.invokeAny(tasks, timeout, unit);
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            throw t;
        }
    }

    private AtomicLong executions = new AtomicLong();
    Set<Runnable> currentlyRunning = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void execute(Runnable command) {
        long x = executions.getAndIncrement();
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) wrapped;
        try {
            int inQueue = tpe.getQueue().size();
            tpe.purge();
            int purged = inQueue - tpe.getQueue().size();

            if (tpe.getMaximumPoolSize() == tpe.getActiveCount() || purged > 0)
                System.out.println("Enter " + x + " >> Queue = " + inQueue + "; purged = " + purged
                        + "; Active = " + tpe.getActiveCount() + "(" + currentlyRunning.size() + ") >> "
                        + command.getClass().getName());

            wrapped.execute(() -> {
                currentlyRunning.add(command);
                command.run();
                currentlyRunning.remove(command);
            });
        } catch(Throwable t) {
            t.printStackTrace();
            t.fillInStackTrace();
            System.out.println("ERROR " + x + " ** Active = " + tpe.getActiveCount()
                    + "(" + currentlyRunning.size() + ") ** " + command.getClass().getName());
            throw t;
        }
//        System.out.println("Exit " + x + " << Active = " + tpe.getActiveCount()
//                + "(" + currentlyRunning.size() + ") << " + command.getClass().getName());
    }
}
