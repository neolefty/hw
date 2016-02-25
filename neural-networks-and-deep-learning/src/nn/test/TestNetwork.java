package nn.test;

import nn.network.*;

import java.util.Random;
import java.util.TreeSet;

/** Test a fully connected neural network. */
public class TestNetwork {
    public static void main(String[] args) throws Exception {
        Stopwatch watch = new Stopwatch();
        TestAll.checkAssertionsEnabled();
        watch.mark("assertions");

        test342();
        watch.mark("simple");

        testSigmoidFunction();
        watch.mark("sigmoid");

        testSeeder();
        watch.mark("seeder");

        System.out.println("Network: Pass -- " + watch);
    }

    /** Test neural net computations using a 3-layer network: 3 inputs, 4 hidden, 2 outputs. */
    public static void test342() {
        Integer[] layerSizes = { 3, 4, 2 };
        FullyConnectedNetwork net
                = new FullyConnectedNetwork(layerSizes, TransferFunction.cappedIdentity);

        NetworkState state = new NetworkState(net);
        double[] inputs = { -1, -0.5, 0.5 };
        double[] weights = {
                // first layer: 3 inputs x 4 hidden = 12 weights
                1, 0.5, 0, -.5,       0.5, 0, 0, -0.5,       0, -1, 1, -0.5,
                // second layer: 4 hidden x 2 outputs = 8 weights
                -1, 1,     0.5, -0.5,     0, 1,      1, 0.5,
        };
        double[] biases = {
                -2, -1.5, -1, 0.25, // 4 hidden neurons
                -.5, 1, // 2 outputs
        };
        state.weights = weights;
        state.biases = biases;
        assert net.getTotalBiasCount() == biases.length;
        assert net.getTotalWeightCount() == weights.length;

        assert state.getWeight(0, 1, 0) == 0.5;
        assert state.getWeight(0, 2, 0) == 0;
        assert state.getWeight(0, 3, 1) == -0.5;
        assert state.getWeight(0, 2, 2) == 1;
        assert state.getWeight(1, 0, 1) == 0.5;

        assert state.getInputWeightSum(1, 1) == -0.5;
        assert state.getInputWeightSum(2, 0) == 0.5;

        double[] out = net.forward(inputs, state);
        // middle layer before biasing: -1.25, -1, 0.5, 0.5
        // middle layer after biasing: 0.75, 0.5, 1.5, 0.25
        // middle layer after capping: 0.75, 0.5, 1, 0.25

        // outputs before biasing: -.25, 1.625
        // outputs after biasing (also capping): 0.25, 0.625
        assert out[0] == 0.25;
        assert out[1] == 0.625;
    }

    private static void testSigmoidFunction() {
        TransferFunction s = TransferFunction.sigmoid;
        assert s.y(0) == 0.5;
        assert s.y(-Double.MAX_VALUE) == 0;
        assert s.y(Double.MAX_VALUE) == 1;
        assert s.y(Math.log(3)) == 0.75;
        assert s.y(-Math.log(3)) == 0.25;
        TestAll.checkCloseTo(s.y(-Double.MAX_VALUE), 0, 1. / 1e20);
        TestAll.checkCloseTo(s.y(Double.MAX_VALUE), 1, 1. / 1e20);

        Random r = new Random();
        Stopwatch t = new Stopwatch();
        int n = 1000000; // a million
        for (int i = 0; i < n; ++i)
            s.y(r.nextDouble());
        t.mark(n + " calculations");

        // test accuracy of inverse
        PseudoGaussianSeeder dice = new PseudoGaussianSeeder(3);
        int deep = 10, nTrials = 1000;
        double errorSum = 0;
        TreeSet<Double> errors = new TreeSet<>();
        for (int i = 0; i < nTrials; ++i) {
            double orig = dice.nextDouble(), next = orig;
            for (int depth = 0; depth < deep; ++depth)
                next = s.y(next);
            for (int depth = 0; depth < deep; ++depth)
                next = s.getInverse().y(next);
            TestAll.checkCloseTo(orig, next, 0.000001);
            double error = Math.abs(next - orig);
            errorSum += error;
            errors.add(error);
        }
        System.out.println("Error sum for " + nTrials + " trials at depth " + deep + ": "
                + errorSum + " (average " + (errorSum / nTrials) + ", max " + errors.descendingIterator().next() + ")");
        t.mark(nTrials + " " + deep + "x inverses");

        System.out.println("Test sigmoid function: " + t);
    }

    private static void testSeeder() {
        // weights are 0, .25, .5, ..75, ..., +1 per layer
        // biases are 0, 1, 2, 3, ..., +5 per layer
        NetworkSeeder seeder = new NetworkSeeder() {
            private double prevWeight = -0.25;
            @Override public double nextWeight(int layer) { return layer + (prevWeight += 0.25); }
            private double prevBias = -1;
            @Override public double nextBias(int layer, int inputCount) { return layer * 5 + (prevBias += 1); }
        };
        FullyConnectedNetwork net = new FullyConnectedNetwork(new Integer[] { 2, 5, 1 });
        NetworkState state = new NetworkState(net);
        state.seed(seeder);

        assert state.weights.length == 15;
        assert state.weights[0] == 0;
        assert state.weights[1] == 0.25;
        assert state.weights[3] == 0.75;
        assert state.weights[9] == 2.25;
        assert state.weights[10] == 3.5;
        assert state.weights[14] == 4.5;

        assert state.biases.length == 6;
        assert state.biases[0] == 5; // biases start in layer 1
        assert state.biases[1] == 6;
        assert state.biases[5] == 15;

        System.out.println(state);

        // make sure we're sufficiently evenly distributed between -1 and 1
        PseudoGaussianSeeder r = new PseudoGaussianSeeder();
        int n = 10000;

        // test bias
        {
            double accum = 0;
            double min = 1, max = -1;
            for (int i = 0; i < n; ++i) {
                double x = r.nextBias(i, n - i);
                min = Math.min(x, min);
                max = Math.max(x, max);
                accum += x;
            }
            TestAll.checkCloseTo(min, -.875, 0.125);
            TestAll.checkCloseTo(max, .875, 0.125);
            TestAll.checkCloseTo(accum, 0, n / 20);
        }

        // test weight
        {
            double accum = 0;
            double min = 1, max = -1;
            for (int i = 0; i < n; ++i) {
                double x = r.nextWeight(i);
                min = Math.min(x, min);
                max = Math.max(x, max);
                accum += x;
            }
            TestAll.checkCloseTo(min, -.875, 0.125);
            TestAll.checkCloseTo(max, .875, 0.125);
            TestAll.checkCloseTo(accum, 0, n / 20);
        }
    }
}
