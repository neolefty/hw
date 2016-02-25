package nn.test;

import nn.network.FullyConnectedNetwork;
import nn.network.NetworkState;
import nn.network.PseudoGaussianSeeder;
import nn.network.TransferFunction;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TestPerformance {
    public static void main(String[] args) throws InterruptedException {
//        testLists();
        Stopwatch total = new Stopwatch();
        TestAll.checkAssertionsEnabled();
        testThreadPool();
        total.mark("thread pool");
        testNeuralNetPerformance();
        total.mark("perf test");
        System.out.println("Performance test complete: " + total);
    }

    private static void testLists() {
        testLists(500);
        testLists(5000);
        testLists(50000);
        testLists(100000);
//        testLists(200000);
//        testLists(500000);
    }

    /** Compare performance of random deletion in linked lists vs array backed lists.
     * @param n the size of lists to use. */
    private static void testLists(int n) {
        Random r = new Random();
        Stopwatch watch = new Stopwatch();

        List<Integer> linked = new LinkedList<>();
        for (int i = 0; i < n; ++i)
            linked.add(r.nextInt());
        watch.mark("create linked list");

        while (!linked.isEmpty())
            linked.remove(r.nextInt(linked.size()));
        watch.mark("empty linked list");

        List<Integer> array = new ArrayList<>();
        for (int i = 0; i < n; ++i)
            array.add(r.nextInt());
        watch.mark("create array list");

        while (!array.isEmpty())
            array.remove(r.nextInt(array.size()));
        watch.mark("empty array list");

        System.out.println(n + ": " + watch);
    }

    public static void testThreadPool() throws InterruptedException {
        testThreadPool(50, 50, 10, 1.5);
        testThreadPool(50, 50, 10, 1.1);
        testThreadPool(500, 10, 20, 1.1);
        testThreadPool(1000, 25, 100, 1.2);
    }

    public static void testThreadPool(int tasks, int msSleep, int threads, double requiredEfficiency)
            throws InterruptedException
    {
        ExecutorService threadPool = Executors.newFixedThreadPool(threads);
        Stopwatch watch = new Stopwatch();
        for (int i = 0; i < tasks; ++i)
            threadPool.submit(() -> {
                try { Thread.sleep(msSleep); } catch (InterruptedException ignored) { }
            });
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.DAYS);
        // 50 tasks * 100 ms / 10 threads = 500 ms
        int expect = tasks * msSleep / threads;
        double max = expect * requiredEfficiency;
        long elapsed = watch.getElapsed();
        System.out.print("Threading test -- elapsed: " + elapsed + " ms; expected " + expect + "; max " + (int) max);
        assert elapsed < max;
        System.out.println(" -- okay");
    }

    public static void testNeuralNetPerformance() throws InterruptedException {
        Integer[] layers = { 784, 15, 10 };
        int networks = 16, iterations = 1000, print = 50000;

        System.out.println("Warmup:");
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks / 2, iterations / 4, 16, print);

        System.out.println();
        System.out.println("Performance Test:");
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks, iterations, 16, print);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks, iterations, 8, print);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks, iterations, 4, print);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks, iterations, 2, print);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks, iterations, 1, print);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks * 2, iterations * 2, 1, print * 4);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks * 5, iterations / 5, 8, print);
        testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks * 10, iterations / 10, 8, print);

        System.out.println();
        testNeuralNetPerformance(layers, TransferFunction.cappedIdentity, networks, iterations, 16, print);
        testNeuralNetPerformance(layers, TransferFunction.cappedIdentity, networks, iterations, 8, print);
        testNeuralNetPerformance(layers, TransferFunction.cappedIdentity, networks, iterations, 1, print);
        testNeuralNetPerformance(layers, TransferFunction.cappedIdentity, networks * 2, iterations * 2, 1, print * 4);

        testCoarse(layers, networks, iterations / 8, print, 8);
        testCoarse(layers, networks, iterations / 4, print, 4);
        testCoarse(layers, networks, iterations / 3, print, 3);
        testCoarse(layers, networks, iterations / 2, print, 2);
    }

    private static void testCoarse(Integer[] layers, int networks, int iterations, int print, int n) throws InterruptedException {
        System.out.println();
        System.out.println("--- course-grain threading: " + n + " threads ---");
        ExecutorService threadPool = Executors.newFixedThreadPool(n);
        Stopwatch watch = new Stopwatch();
        AtomicLong multiplies = new AtomicLong();
        for (int i = 0; i < n; ++i)
            threadPool.submit(() -> {
                long mults = testNeuralNetPerformance(layers, TransferFunction.sigmoid, networks, iterations, 1, print);
                multiplies.addAndGet(mults);
            });
        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.MINUTES);
        double multsPerNs = (double) multiplies.get() / watch.getElapsed() / 1000000;
        System.out.println("--- elapsed: " + watch.getElapsed() + " for " + multiplies
                + " multiplies - " + multsPerNs + " per ns ---");
    }

    /** Returns the number of multiplies. */
    public static long testNeuralNetPerformance
            (Integer[] layerSizes, TransferFunction f, int networks, int iterations, int threads, int howOftenToPrint)
    {
        Stopwatch watch = new Stopwatch();

        // TODO why is this so much faster in a single thread?
        // What if we put the NetworkStates each into their own thread -- put their creation inside the task?

        ExecutorService threadPool;
        if (threads == 1)
            threadPool = Executors.newSingleThreadExecutor();
        else
            threadPool = Executors.newFixedThreadPool(threads);
        FullyConnectedNetwork network = new FullyConnectedNetwork(layerSizes, f);
        PseudoGaussianSeeder seeder = new PseudoGaussianSeeder();

        List<NetworkState> states = new ArrayList<>();
        for (int i = 0; i < networks; ++i) {
            NetworkState state = new NetworkState(network);
            state.seed(seeder);
            states.add(state);
        }
        watch.mark("create");

//        List<Future<double[]>> results = new Vector<>(); // thread-safe
        final long[] count = {0};
        int batch = 10;
        for (NetworkState state : states) {
            for (int i = 0; i < iterations / batch; ++i) {
                Callable<double[]> task = () -> {
                    double[] result = null;
                    for (int j = 0; j < batch; ++j) {
                        double[] inputs = new double[network.getInputCount()];
                        for (int i1 = 0; i1 < inputs.length; ++i1)
                            inputs[i1] = seeder.nextWeight(0);
                        long c = ++count[0];
                        result = network.forward(inputs, state);
                        if (c % howOftenToPrint == 0)
                            System.out.println("    " + c + ": " + Arrays.toString(result));
                    }
                    return result;
                };
//                results.add(threadPool.submit(task));
                threadPool.submit(task);
            }
        }
        watch.mark("queue");

        try {
            threadPool.shutdown();
            threadPool.awaitTermination(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        watch.mark("forward");

        long multiplies = network.getTotalWeightCount() * count[0];
        long multsPerMs = multiplies / watch.getElapsed("forward");
        double multsPerNs = (double) multsPerMs / 1e6;
                System.out.println(watch.getElapsed("forward") + " ms: " + threads + " threads, "
                + networks + " networks (" + Arrays.toString(layerSizes)
                + ", " + network.getTotalBiasCount() + " biases, " + network.getTotalWeightCount() + " weights) "
                + "x " + iterations + " iterations " + "(" + count[0] + ")" + ", "
                + f.toString() + " transfer function -- " + watch
                + " -- " + multsPerMs + " multiplies per ms (" + multsPerNs + " per ns)");
        return multiplies;
    }
}
