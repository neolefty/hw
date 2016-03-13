package org.neolefty.cs143.hybrid_images.util;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/** Wraps an ExecutorService and automatically cancels future operations that have been superceded. */
public class CancellingExecutor {
    private ExecutorService wrapped;
    private boolean mayInterrupt = false;
    private static final boolean DEBUG = false;

    /** Map of execution keys to pending operations. */
    private final Map<Object, Set<Future>> inFlight = Collections.synchronizedMap(new HashMap<>());

    public CancellingExecutor(ExecutorService wrapped) {
        this.wrapped = wrapped;
    }

    /** When cancelling future operations, should they be interrupted if already running, or allowed to complete? */
    public void setMayInterrupt(boolean mayInterrupt) {
        this.mayInterrupt = mayInterrupt;
    }

    /** Submit a group of tasks to be run.
     *  First, cancel any previous runnables submitted with the same key. */
    public Collection<Future> submit(Object key, Collection<Runnable> runnables) {
        // synchronize the whole block to prevent mixing submissions with the same key
        synchronized (inFlight) {
            int cancelled = 0, total = 0;
            Set<Future> futures = inFlight.get(key);
            if (futures == null) {
                futures = Collections.synchronizedSet(new HashSet<>());
                inFlight.put(key, futures);
            } else {
                total = futures.size();
                for (Future future : futures)
                    if (!future.isDone() && future.cancel(mayInterrupt)) // warning: side effect in condition
                        ++cancelled;
                futures.clear();
//                if (total > 0)
//                    System.out.println("Cancelled " + cancelled + " / " + total + " -- " + key);
            }
            List<Future> result = new ArrayList<>();
            for (Runnable runnable : runnables)
                result.add(wrapped.submit(runnable));
            futures.addAll(result);
            if (DEBUG)
                System.out.printf("Cancelled: " + cancelled);
            return result;
        }
    }

    /** Submit a task to be run.
     *  First, cancel any previous runnables submitted with the same key. */
    public Future submit(Object key, Runnable runnable) {
        return submit(key, Collections.singleton(runnable)).iterator().next();
    }
}
